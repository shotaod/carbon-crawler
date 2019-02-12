import './helper/extend.prototype'
import {MySQL, sql} from "./helper/sqls";
import * as path from "path";
import {asyncMakeDir, asyncReadDir, asyncReadYaml, asyncUnLink, asyncWriteFile} from "./helper/files";
import {Cardinality} from "./helper/cardinalities";
import {mapDBType, mapKType} from "./helper/typeMappings";
import {domainKtTemplate, Table} from "./template/domain";

const config = {
    host: 'rdb.carbon.local',
    port: 40003,
    user: 'carbon',
    password: 'carbonpw',
    database: 'crawlerdb',
    packagePath: '../carbon-crawler-model/src/main/kotlin/org/carbon/crawler/model/domain',
    testPackagePath: '../carbon-crawler-model/src/test/kotlin/org/carbon/crawler/model/domain',
    excludeTables: [
        'flyway_schema_history',
    ],
    tablePackage: 'org.carbon.crawler.model.infra.record',
    basePackage: 'org.carbon.crawler.model.domain',
};

const predefinedColumn = [
    'id',
    'ins_at',
    'upd_at',
    'del_at',
]

type DescResult = {
    Field: string   // 'id',
    Type: string    // 'bigint(20)',
    Null: string    // 'NO',
    Key: string     // 'PRI',
    Default: string // null,
    Extra: string   // 'auto_increment'
}

type EntitySchema = {
    domains: {
        [NAME in string]: {
            base: string
            join1?: string[]
            joinN?: string[]
        }
    }
}

const cardinality = new Cardinality(config.database);

async function extractTable(tableName: string) {
    const desc = await sql<DescResult>`DESC ${tableName}`;
    const foreignParentKeys = await cardinality.selectParent(tableName);

    // ______________________________________________________
    //
    // @ scalar column
    const columns = desc.rows
        .filter(row => !predefinedColumn.some(p => p === row.Field))
        .filter(row => foreignParentKeys.length === 0 || foreignParentKeys.some(p => p.fromColumnName !== row.Field))
        .map(row => {
            return {
                name: row.Field,
                kType: mapKType(row.Type, row.Null === 'YES'),
                dbType: mapDBType(row.Type, row.Null === 'YES'),
            }
        })

    // ______________________________________________________
    //
    // @ foreign column
    const foreignColumns = desc.rows
        .filter(row => foreignParentKeys.some(p => p.fromColumnName === row.Field))
        .map(row => {
            const ref = foreignParentKeys.find(p => p.fromColumnName === row.Field)!
            return {
                name: row.Field,
                table: ref.toTableName,
            }
        })
    return {
        name: tableName,
        columns,
        foreignColumns,
    }
}

MySQL.run(config, async () => {
    const packages = ['base', 'impl/stub', 'repo']
        .flatMap(pa => [
            path.resolve(config.packagePath, pa),
            path.resolve(config.testPackagePath, pa),
        ]);

    // ______________________________________________________
    //
    // @ clean
    try {
        await Promise.all(packages.map(dir => asyncMakeDir(dir, {recursive: true})))
    } catch (ignore) {
    }
    const chunk = await Promise.all(packages.map(async dir => {
        const files = await asyncReadDir(dir);
        return files.map(f => path.resolve(dir, f));
    }));
    console.log('clean dist', chunk.flatMap(c => [...c]));
    await Promise.all(chunk.flatMap(c => [...c])
        .map(file => asyncUnLink(file)));

    // ______________________________________________________
    //
    // @ parse yaml & db definition
    const entitySchema = await asyncReadYaml<EntitySchema>('../carbon-crawler-model/schema/domain/index.yml')
    const parseSchema = Object.keys(entitySchema.domains)
        .map(domainName => ({
            domainName,
            info: entitySchema.domains[domainName],
        }))
        .map(({domainName, info: {join1, joinN, ...rest}}) => ({
            domainName,
            info: {
                ...rest,
                join1: join1 || [],
                joinN: joinN || [],
            }
        }));

    const tables = parseSchema
        .flatMap(({info: {base, join1, joinN}}) => [base, ...join1, ...joinN])
        .unique();

    const tableInfo = (await Promise.all(tables.map(table => extractTable(table))))
        .reduce((acc, el) => ({
            ...acc,
            ...{
                [el.name]: el
            }
        }), {} as { [k in string]: Table });

    const files = parseSchema
        .flatMap(({domainName, info}) => {
            const {base, impl, repo, testRepo} = domainKtTemplate({
                domainName,
                basePackage: config.basePackage,
                tablePackage: config.tablePackage,
                baseTable: tableInfo[info.base],
                refTables: [
                    ...info.join1.map(t => ({...tableInfo[t], many: false})),
                    ...info.joinN.map(t => ({...tableInfo[t], many: true})),
                ],
                tableInfo,
            });
            return [
                {
                    filePath: path.resolve(config.packagePath, `base/${domainName}.kt`),
                    raw: base
                },
                {
                    filePath: path.resolve(config.packagePath, `impl/stub/${domainName}.kt`),
                    raw: impl
                },
                {
                    filePath: path.resolve(config.packagePath, `repo/${domainName}.kt`),
                    raw: repo
                },
                {
                    filePath: path.resolve(config.testPackagePath, `repo/${domainName}Test.kt`),
                    raw: testRepo
                },
            ]
        });

    await Promise.all(files.map(({filePath, raw}) => asyncWriteFile(path.resolve(config.packagePath, filePath), raw)))
    console.log(`
--------------------------------------------------
generate result
--------------------------------------------------
package: ${config.packagePath}

${files.map(it => it.filePath).join('\n')}
`);
});
