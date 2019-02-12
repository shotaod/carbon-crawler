import './helper/extend.prototype'
import * as path from 'path';
import {recordTemplateKt} from "./template/infra.record";
import {camel, pascal} from "./helper/strings";
import {MySQL, sql} from "./helper/sqls";
import {asyncReadDir, asyncUnLink, asyncWriteFile} from "./helper/files";
import {Cardinality} from "./helper/cardinalities";
import {mapKDBType} from "./helper/typeMappings";

const config = {
    host: 'rdb.carbon.local',
    port: 40003,
    user: 'carbon',
    password: 'carbonpw',
    database: 'crawlerdb',
    packagePath: '../carbon-crawler-model/src/main/kotlin/org/carbon/crawler/model/infra/record',
    excludeTables: [
        'flyway_schema_history',
    ],
};

const basePackage = 'package org.carbon.crawler.model.infra.record';

const baseImports = [
    'org.carbon.crawler.model.extend.exposed.AuditEntity',
    'org.carbon.crawler.model.extend.exposed.AuditUUIDTable',
    'org.carbon.crawler.model.extend.exposed.EagerTrait',
    'org.jetbrains.exposed.dao.EntityClass',
    'org.jetbrains.exposed.dao.EntityID',
].map(i => `import ${i}`)

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

const cardinality = new Cardinality(config.database);

async function generate(tableName: string) {
    if (config.excludeTables.some(ex => ex === tableName)) return;

    const className = pascal(tableName);
    const recordFields: string[] = [];
    const columnFields: string[] = [];

    // reference
    const parentRefField = (
        fromTableName: string,
        foreignKeyColumn: string,
        parentTableName: string
    ) => `var ${camel(parentTableName)} by ${pascal(parentTableName)}Record referencedOn ${pascal(fromTableName)}Table.${camel(foreignKeyColumn)}`
    const parentRefColumn = (
        parentTableName: string,
        foreignKeyColumn: string,
    ) => `val ${camel(foreignKeyColumn)} = reference("${foreignKeyColumn}", ${pascal(parentTableName)}Table)`
    const childRefField = (
        childTableName: string,
        foreignKeyColumn: string,
    ) => `val ${camel(childTableName)} by ${pascal(childTableName)}Record referrersOn ${pascal(childTableName)}Table.${camel(foreignKeyColumn)}`

    // scalar column
    const recordFieldTmpl = (propertyName: string) => `var ${propertyName} by ${className}Table.${propertyName}`;
    const columnFieldTmpl = (propertyName: string, type: string) => `val ${propertyName} = ${type}`;
    const columnTypeTmpl = (type: string, physicalName: string, length: string | undefined, options: string[]) =>
        `${type}("${physicalName}"${length ? ', ' + length : ''})${options.map(o => `.${o}`).join('')}`;
    // columnOptions
    const nullableType = (row: DescResult) => row.Null !== 'NO' ? 'nullable()' : '';

    // check parent referer
    const parents = await cardinality.selectParent(tableName);
    parents.forEach(info => {
        recordFields.push(parentRefField(tableName, info.fromColumnName, info.toTableName));
        columnFields.push(parentRefColumn(info.toTableName, info.fromColumnName));
    });

    // check child referer
    const children = await cardinality.selectChild(tableName);
    children.forEach(info => {
        recordFields.push(childRefField(info.fromTableName, info.fromColumnName))
    });

    if (parents.length > 0 || children.length > 0) {
        recordFields.push("");
        columnFields.push("");
    }

    const {rows} = await sql<DescResult>`DESC ${tableName}`;
    rows.forEach(row => {

        // excludes
        if (predefinedColumn.indexOf(row.Field) >= 0) return;
        if (parents.some(p => p.fromColumnName === row.Field)) return;

        const propertyName = camel(row.Field);
        const physicalName = row.Field;
        const {type, param} = mapKDBType(row.Type);

        recordFields.push(recordFieldTmpl(propertyName));
        const columnOptions = [
            nullableType(row),
        ].filter(Boolean)
        columnFields.push(columnFieldTmpl(propertyName, columnTypeTmpl(type, physicalName, param, columnOptions)));
    })

    const ktFile = recordTemplateKt({
        packages: basePackage,
        imports: baseImports,
        tableName,
        className,
        columnFields: columnFields,
        recordFields: recordFields
    });

    const fileName = `${pascal(tableName)}.kt`;
    console.log('--------------------------------------------------');
    console.log(fileName);
    console.log('--------------------------------------------------');
    console.log(ktFile);
    console.log();

    await asyncWriteFile(path.resolve(config.packagePath, fileName), ktFile);
    return fileName;
}

MySQL.run(config, async () => {
    const dir = await asyncReadDir(config.packagePath);
    const removePromise = dir.map(file => asyncUnLink(path.resolve(config.packagePath, file)));
    await Promise.all(removePromise)

    const {rows, fields} = await sql`SHOW tables`;
    const tables = rows.map(row => row[fields[0].name] as string);

    const res = await Promise.all(tables.map(generate))
    const files = res.filter(Boolean) as string[]

    console.log(`
--------------------------------------------------
generate result
--------------------------------------------------
package: ${config.packagePath}

files: 
${files.join('\n')}
`);

});
