import * as random from 'randomstring'
import * as _ from 'lodash'
import moment from "moment";
import uuid from 'uuid/v4'
import {camel, pascal} from "../../helper/strings";
import {Table, TemplateArgs} from "./index";

type ForeignId = {
    idVarName: string
    tableName: string
}

export function domainTestRepoTmplKt(
    {
        domainName,
        ettBaseClassName,
        className,
        basePackage,
        tablePackage,
        tableImport,
        recordImport,
        baseTable,
        refTables,
        additionalImport,
        tableInfo,
    }: TemplateArgs & {
        ettBaseClassName: string
        tableInfo: {
            [k in string]: Table
        }
    }
) {
    const imports = [
        'import org.carbon.crawler.model.extend.kompose.DBUtil',
        'import org.carbon.crawler.model.extend.kompose.RollbackTransaction',
        'import org.carbon.crawler.model.test.DatabaseTest',
        'import org.carbon.kompose.kompose',
        'import org.junit.jupiter.api.Test',
        'import org.jetbrains.exposed.dao.EntityID',
        'import org.jetbrains.exposed.sql.*',
        'import io.kotlintest.*',
        `import ${basePackage}.base.${pascal(baseTable.name)}ID`,
        `import ${basePackage}.base.${ettBaseClassName}`,
        ...baseTable.foreignColumns.map(foreign => `import ${basePackage}.base.${pascal(foreign.table)}ID`),
        ...tableImport,
        ...additionalImport,
        // todo resolve more gracefully...
        ...baseTable.foreignColumns.map(foreign => `import org.carbon.crawler.model.infra.record.${pascal(foreign.table)}Table`),
    ].sort().unique()

    const TEST_FUN_SIG = (name: string) => `@Test
fun ${name}() = kompose(RollbackTransaction)`

    const GIVEN_BLOCK = `
// ______________________________________________________
//
// @ Given
with(context[DBUtil::class]) {
    clean()
}`;

    const WHEN_BLOCK = `
// ______________________________________________________
//
// @ When`;

    const THEN_BLOCK = `
// ______________________________________________________
//
// @ Then`;

    const kEttTypeDataFunMap = {
        varchar: (p?: string) => `"${random.generate(parseInt(p!))}"`,
        char: (p?: string) => `"${random.generate(parseInt(p!))}"`,
        text: () => `"${random.generate(1025)}"`,
    } as { [k in string]: (p?: string) => string }

    const declareTestData = (dbType: { type: string, param?: string, nullable: boolean }) => {
        const maybe = kEttTypeDataFunMap[dbType.type]
        if (!maybe) throw Error(`Missing test data generator for type ${dbType.type}`)

        const varName = ['test_data', dbType.type, dbType.param, random.generate(5)].filter(Boolean).join('_')
        const expr = `val ${varName} = ${maybe(dbType.param)}`
        return {
            expr,
            varName,
        }
    }

    function idExpr(table: Table) {
        const EntityID = (tableName: string) => `EntityID("${uuid().replace(/-/g, '')}", ${pascal(tableName)}Table)`
        const idVarName = `id_${random.generate(5)}`
        const expr = `val ${idVarName} = ${EntityID(table.name)}`
        return {
            idVarName,
            expr,
        }
    }

    function testDataExpr(
        table: Table,
        varNameDict: any,
        template: string[],
        foreignIds: ForeignId[],
        skip: boolean = false) {

        const _idVarName = _.get(varNameDict, [table.name, 'id']);
        if (_idVarName) return _idVarName;

        const cache = [] as string[];
        const {idVarName, expr} = idExpr(table);
        cache.push(expr);

        // assign column expr
        const columnAssignExprs = table.columns.map(col => {
            const {expr, varName} = declareTestData(col.dbType);
            cache.push(expr);

            // register varName
            _.set(varNameDict, [table.name, col.name], varName)
            return `it[this.${camel(col.name)}] = ${varName}`
        });

        // register id
        _.set(varNameDict, [table.name, 'id'], idVarName)

        if (skip) return idVarName;

        template.push(`// ----- setup ${table.name} -----`)
        template.push(...cache)

        template.push(`${pascal(table.name)}Table.insert {
    it[this.id] = ${idVarName}
    ${foreignIds.map(fVar => `it[this.${camel(fVar.tableName)}Id] = ${fVar.idVarName}`).join('\n    ')}
    ${columnAssignExprs.join('\n    ')}
}`)
        return idVarName;
    }

    function ancestors(table: Table) {
        return table.foreignColumns
            .map(f => f.table)
    }

    const aggregation = [
        baseTable.name,
        ...ancestors(baseTable),
        ...refTables.map(ref => ref.name)
    ];

    function descendants(table: Table) {
        return Object.keys(tableInfo)
            .map(key => tableInfo[key])
            .filter(t => aggregation.includes(t.name))
            .filter(t => t.foreignColumns.some(f => f.table === table.name))
    }

    // recursive
    function setupDataToAncestor(table: Table,
                                 template: string[],
                                 varNameDict: any,
                                 skip: boolean = false): string {
        const foreignIds = ancestors(table)
            .map(foreignTable => ({
                tableName: foreignTable,
                idVarName: setupDataToAncestor(tableInfo[foreignTable], template, varNameDict),
            }));
        return testDataExpr(table, varNameDict, template, foreignIds, skip);
    }

    // recursive
    function setupDataToDescendant(table: Table,
                                   template: string[],
                                   varNameDict: any,
                                   foreignIds: ForeignId[],
                                   skip: boolean = false) {
        const idVarName = testDataExpr(table, varNameDict, template, foreignIds, skip);
        descendants(table)
            .map(descendantTable => setupDataToDescendant(descendantTable, template, varNameDict, [{
                idVarName,
                tableName: table.name
            }]))
    }

    function insertData() {
        const template: string[] = [];
        const varNameDict = {} as any;
        const idVarName = setupDataToAncestor(baseTable, template, varNameDict);
        const foreignIds = ancestors(baseTable).map(t => ({
            idVarName: varNameDict[t],
            tableName: t
        }))
        setupDataToDescendant(baseTable, template, varNameDict, foreignIds, true)
        return {template, idVarName, varNameDict};
    }

    // ______________________________________________________
    //
    // @ for save
    function entityNewExpr(varNameDict: any) {
        const testDataDeclareExprs = [] as string[]
        const newArgExprs = [] as string[]

        // ancestor value
        ancestors(baseTable).forEach(f => {
            newArgExprs.push(`${pascal(f)}ID(${varNameDict[f].id}.value)`)
        });
        // column value
        baseTable.columns.forEach(col => {
            const {expr, varName} = declareTestData(col.dbType);
            _.set(varNameDict, [baseTable.name, col.name], varName)

            testDataDeclareExprs.push(expr)
            newArgExprs.push(varName)
        })
        // descendant value
        descendants(baseTable).forEach(desTable => {
            const descendantClass = pascal(desTable.name);
            const descendantArgs = desTable.columns
                .map(col => {
                    const {varName, expr} = declareTestData(col.dbType);
                    _.set(varNameDict, [desTable.name, col.name], varName);
                    testDataDeclareExprs.push(expr)
                    return varName
                });
            const many = refTables.find(ref => ref.name === desTable.name)!.many;
            let constructor = `${descendantClass}(
${['nextId()', ...descendantArgs].join(',\n').indent(4)}
)`
            if (many) {
                constructor = `listOf(
${constructor.indent(4)}
)`
            }
            newArgExprs.push(`{ nextId ->
${constructor.indent(4)}
}`)
        });

        const NEW_EXPR = `${testDataDeclareExprs.join('\n')}
val entity = ${ettBaseClassName}.new(
${newArgExprs.join(',\n').indent(4)}
)
val entityID = entity.id.dbValue
`
        _.set(varNameDict, [baseTable.name, 'id'], 'entityID')

        return {
            varNameDict,
            NEW_EXPR,
        }
    }

    function ASSERT_FROM_DB(varNameDict: any) {
        return [baseTable, ...descendants(baseTable)]
            .map(table => {
                const TableClass = `${pascal(table.name)}Table`
                const FOREIGN_KEY_ASSERT = table.foreignColumns
                    .map(f => `this[${TableClass}.${camel(f.table)}Id] shouldBe ${varNameDict[f.table].id}`)
                    .join('\n')
                const COLUMN_ASSERT = table.columns
                    .map(col =>
                        `this[${TableClass}.${camel(col.name)}] shouldBe ${varNameDict[table.name][col.name]}`)
                    .join('\n')
                return `with(${TableClass}.selectAll().single()) {
${FOREIGN_KEY_ASSERT.indent(4)}
${COLUMN_ASSERT.indent(4)}
}`
            }).join('\n')
    }

    function TEST_SAVE_FUNCTION() {
        const varNameDict: any = {}
        const ancestorTemplate: string[] = []
        setupDataToAncestor(baseTable, ancestorTemplate, varNameDict, true)
        const {NEW_EXPR} = entityNewExpr(varNameDict);
        const SET_ANCESTOR_DATA = ancestorTemplate.join('\n')

        return `
${TEST_FUN_SIG('save')} {
${GIVEN_BLOCK.indent(4)}
${SET_ANCESTOR_DATA.indent(4)}

${WHEN_BLOCK.indent(4)}
${NEW_EXPR.indent(4)}
    ${pascal(domainName)}Repository.save(entity)

${THEN_BLOCK.indent(4)}
${ASSERT_FROM_DB(varNameDict).indent(4)}
}`
    }

    function TEST_REMOVE_FUNCTION() {
        const {template, idVarName} = insertData();
        const INSERT_DATA = template.join('\n');
        const repositoryClass = `${domainName}Repository`;
        const REM_EXPR = `val entity = ${repositoryClass}.fetch(${pascal(baseTable.name)}ID(${idVarName}.value))
${repositoryClass}.remove(entity)
`
        const ASSERT_ZERO_RECORD_EXPR = [
            `${pascal(baseTable.name)}Table`,
            ...refTables.map(ref => `${pascal(ref.name)}Table`),
        ]
            .map(tableClass => `${tableClass}.selectAll().count() shouldBe 0`)
            .join('\n')

        return `
${TEST_FUN_SIG('remove')} {
${GIVEN_BLOCK.indent(4)}
${INSERT_DATA.indent(4)}

${WHEN_BLOCK.indent(4)}
${REM_EXPR.indent(4)}

${THEN_BLOCK.indent(4)}
${ASSERT_ZERO_RECORD_EXPR.indent(4)}
}`
    }

    // ____________________________________________________________________________________________________________
    //
    // @ Templates
    return `@file:Suppress("MoveLambdaOutsideParentheses", "LocalVariableName")

package ${basePackage}.repo

${imports.join('\n')}

/**
 * @author Soda(by carbon-scripts.gen.domain.test.repo.ts) ${moment().format("LL.")}
 */
internal class ${className} : DatabaseTest {

${TEST_FUN_SIG('fetchId').indent(4)} {
${GIVEN_BLOCK.indent(8)}
${(() => {
        const {template, idVarName, varNameDict} = insertData();
        return `
${template.join('\n').indent(8)}

${WHEN_BLOCK.indent(8)}
        val r = ${pascal(domainName)}Repository.fetch(${pascal(baseTable.name)}ID(${idVarName}.value))

${THEN_BLOCK.indent(8)}
        // id assertion
        r.id.value shouldBe ${idVarName}.value
        // scalar field assertion
        ${baseTable.columns
            .map(col => `r.${camel(col.name)} shouldBe ${varNameDict[baseTable.name][col.name]}`)
            .join('\n        ')}
        // aggregate subs assertion
        ${descendants(baseTable)
            .map(descendant => {
                const many = refTables.find(ref => ref.name === descendant.name)!.many;
                return `with(r.${camel(descendant.name)}${many ? '[0]' : ''}) {
            id.value shouldBe ${varNameDict[descendant.name].id}.value
            ${descendant.columns
                    .map(col => `${camel(col.name)} shouldBe ${varNameDict[descendant.name][col.name]}`)
                    .join('\n            ')}
        }`
            })
            .join('\n        ')}
`
    })()}
    }
    
${TEST_SAVE_FUNCTION().indent(4)}

${TEST_REMOVE_FUNCTION().indent(4)}

}
`.formatKt()
}
