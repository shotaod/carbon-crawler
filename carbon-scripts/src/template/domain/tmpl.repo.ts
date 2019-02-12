import moment from "moment";
import {camel, pascal} from "../../helper/strings";
import {TemplateArgs} from "./index";

export function domainRepoTmplKt(
    {
        domainName,
        className,
        basePackage,
        tablePackage,
        tableImport,
        recordImport,
        baseTable,
        refTables,
        additionalImport,
    }: TemplateArgs
) {
    const imports = [
        `import ${basePackage}.base.${domainName}ID`,
        `import ${basePackage}.impl.${domainName}Entity`,
        `import ${basePackage}.shared.DelegatePageReader`,
        `import ${basePackage}.shared.DelegateWriter`,
        `import ${basePackage}.shared.PageReader`,
        `import ${basePackage}.shared.Reader`,
        `import ${basePackage}.shared.Writer`,
        `import org.carbon.crawler.model.extend.exposed.batchInsertV2`,
        `import org.jetbrains.exposed.sql.ResultRow`,
        `import org.jetbrains.exposed.sql.deleteWhere`,
        `import java.time.LocalDateTime`,
        ...baseTable.foreignColumns.map(foreign => `import ${basePackage}.base.${pascal(foreign.table)}ID`),
        ...refTables.map(ref => `import ${basePackage}.base.${pascal(ref.name)}ID`),
        ...tableImport,
        ...recordImport,
        ...additionalImport,
    ].sort()

    const ormFunction = {
        insert: 'entityToInsert',
        delete: 'entityToDelete',
        mapping: 'recordToEntity',
    }

    const GENERIC = `${pascal(baseTable.name)}ID, ${domainName}Entity`

    const baseTableClass = `${pascal(baseTable.name)}Table`
    const innerJoinClasses = refTables.filter(ref => !ref.many).map(ref => ` innerJoin ${pascal(ref.name)}Table`)
    const leftJoinsClasses = refTables.filter(ref => ref.many).map(ref => ` leftJoin ${pascal(ref.name)}Table`)
    const ID_COLUMN = `${baseTableClass}.id`
    const INNER_JOIN_CLAUSE = baseTableClass + innerJoinClasses
    const FULL_JOIN_CLAUSE = baseTableClass + innerJoinClasses + leftJoinsClasses

    const insertMappings = [
        ...baseTable.foreignColumns
            .map(fCol => `this[${pascal(baseTable.name)}Table.${camel(fCol.name)}] = it.${camel(fCol.name)}.dbValue`),
        ...baseTable.columns
            .map(col => `this[${pascal(baseTable.name)}Table.${camel(col.name)}] = it.${camel(col.name)}`)
    ]

    const ID_RECORD_GENERIC = `${pascal(baseTable.name)}ID, ${pascal(baseTable.name)}Record`

    return `@file:Suppress("MoveLambdaOutsideParentheses")

package ${basePackage}.repo

${imports.join('\n')}

/**
 * @author Soda(by carbon-scripts.gen.domain.repo.ts) ${moment().format("LL.")}
 */
// ______________________________________________________
//
// @ I/O Repository
object ${className} :
    Writer<${GENERIC}> by DelegateWriter(
        bulkInsert = ::${ormFunction.insert},
        bulkDelete = ::${ormFunction.delete}
    ),
    Reader<${GENERIC}>,
    PageReader<${GENERIC}> by DelegatePageReader(
        idColumn = ${ID_COLUMN},
        inner = ${INNER_JOIN_CLAUSE},
        full = ${FULL_JOIN_CLAUSE},
        rowMapper = ::${ormFunction.mapping}
    )

// ______________________________________________________
//
// @ O/R Mapping Function
private fun ${ormFunction.insert}(entities: List<${domainName}Entity>) {
    val now = LocalDateTime.now()
    ${pascal(baseTable.name)}Table.batchInsertV2(
        data = entities,
        body = {
            this[${pascal(baseTable.name)}Table.id] = it.id.dbValue
            ${insertMappings.join('\n            ')}
        },
        onDuplicate = {
            this[${pascal(baseTable.name)}Table.updatedAt] = now
            ${insertMappings.join('\n            ')}
        })

    // 1to1
    ${refTables.filter(it => !it.many).map(table => {
        const Table = pascal(table.name) + 'Table';
        const insertMappings = table.columns.map(col =>
            `this[${Table}.${camel(col.name)}] = it.${camel(table.name)}.${camel(col.name)}`)
        return `${Table}.batchInsertV2(
        data = entities,
        body = {
            this[${Table}.id] = it.${camel(table.name)}.id.dbValue
            this[${Table}.${camel(baseTable.name)}Id] = it.id.dbValue
            ${insertMappings.join('\n            ')}
        },
        onDuplicate = {
            this[${Table}.updatedAt] = now
            this[${Table}.${camel(baseTable.name)}Id] = it.id.dbValue
            ${insertMappings.join('\n            ')}
        })`;
    })}

    // 1toN
    ${refTables.filter(it => it.many).map(table => {
        const Table = pascal(table.name) + "Table";
        const varTable = camel(table.name);
        const insertMappings = table.columns.map(col =>
            `this[${Table}.${camel(col.name)}] = _ref.${camel(col.name)}`);
        return `val ${varTable} = entities.flatMap { ent -> ent.${varTable}.map { ref -> ent.id to ref } }
    ${Table}.deleteWhere {
        ${Table}.${camel(baseTable.name)}Id inList ${varTable}.map { (_, ref) -> ref.id.value }
    }
    ${Table}.batchInsertV2(
        data = ${varTable},
        body = { (_id, _ref) ->
            this[${Table}.id] = _ref.id.dbValue
            this[${Table}.${camel(baseTable.name)}Id] = _id.dbValue
            ${insertMappings.join('\n            ')}
        })`;
    })}
}

private fun ${ormFunction.delete}(entities: List<${domainName}Entity>) {
    val ids = entities.map { it.id.value }
    ${refTables.filter(ref => ref.many).map(ref =>
        `${pascal(ref.name)}Table.deleteWhere { ${pascal(ref.name)}Table.${camel(baseTable.name)}Id inList ids }`).join('\n    ')}
    ${refTables.filter(ref => !ref.many).map(ref =>
        `${pascal(ref.name)}Table.deleteWhere { ${pascal(ref.name)}Table.${camel(baseTable.name)}Id inList ids }`).join('\n    ')}
    ${pascal(baseTable.name)}Table.deleteWhere { ${pascal(baseTable.name)}Table.id inList ids }
}

private fun ${ormFunction.mapping}(rows: List<ResultRow>): List<${pascal(baseTable.name)}Entity> {
    val bases = mutableMapOf<${ID_RECORD_GENERIC}>()
    ${refTables.filter(ref => !ref.many)
        .map(table =>
            `val ${camel(table.name)}Map = mutableMapOf<${pascal(baseTable.name)}ID, Pair<${pascal(table.name)}ID, ${pascal(table.name)}Record>>()`)
        .join('\n    ')
        }
    ${refTables.filter(ref => ref.many)
        .map(table =>
            `val ${camel(table.name)}Map = mutableMapOf<${pascal(baseTable.name)}ID, MutableMap<${pascal(table.name)}ID, ${pascal(table.name)}Record>>()`)
        .join('\n    ')
        }

    rows.forEach { row ->
        val id = ${`${pascal(baseTable.name)}ID(row[${pascal(baseTable.name)}Table.id].value)`}
        bases.getOrPut(id) { ${pascal(baseTable.name)}Record.wrapRow(row) }
        ${refTables.filter(ref => !ref.many)
        .map(table => `${camel(table.name)}Map.getOrPut(id) { ${pascal(table.name)}ID(row[${pascal(table.name)}Table.id].value) to ${pascal(table.name)}Record.wrapRow(row) }`)
        .join('\n        ')
        }
        ${refTables.filter(ref => ref.many)
        .map(table => `row.tryGet(${pascal(table.name)}Table.id)
            ?.also {
                ${camel(table.name)}Map
                    .getOrPut(id) { mutableMapOf() }
                    .getOrPut(${pascal(table.name)}ID(it.value)) { ${pascal(table.name)}Record.wrapRow(row) }
            }`)
        .join('\n        ')
        }
    }
    return bases.map { (_id, _record) ->
        ${pascal(baseTable.name)}Entity(
${[
        '_id',
        ...baseTable.foreignColumns
            .map(foreign => `${pascal(foreign.table)}ID(_record.${camel(foreign.table)}.id.value)`),
        ...baseTable.columns.map(col => `_record.${camel(col.name)}`),
        ...refTables.filter(r => !r.many).map(table => `{
    val (__id, __record) = ${camel(table.name)}Map[id]!!
    ${pascal(table.name)}(
        __id,
        ${table.columns.map(col => `__record.${camel(col.name)}`).join(',\n        ')}
    )
}`),
        ...refTables.filter(r => r.many).map(table => `{
    ${camel(table.name)}Map[_id]?.map { (__id, __record) ->
        ${pascal(table.name)}(
            __id,
            ${table.columns.map(col => `__record.${camel(col.name)}`).join(',\n            ')}
        )
    }.orEmpty()
}`),
    ].map(l => `${l}`).join(',\n').replace(/^(.*)$/gm, '            $1')})
    }
}
`.formatKt()
}
