import {camel, pascal} from "../../helper/strings";
import {domainBaseTemplateKt} from "./tmpl.entity.base";
import {domainImplTemplateKt} from "./tmpl.entity.impl";
import {plural} from 'pluralize'
import {domainRepoTmplKt} from "./tmpl.repo";
import {domainTestRepoTmplKt} from "./tmpl.test.repo";

export type Table = {
    name: string
    columns: {
        name: string
        kType: string
        dbType: {
            type: string
            param?: string
            nullable: boolean
        }
    }[],
    foreignColumns: {
        name: string
        table: string
    }[]
}

export type RefTable = Table & {
    many: boolean
}

export type TemplateArgs = {
    domainName: string,
    basePackage: string,
    tablePackage: string,
    baseTable: Table,
    refTables: RefTable[],
    className: string,
    additionalImport: string[],
    tableImport: string[],
    recordImport: string[],
    entityConstructorArgs: string[],
}

export function domainKtTemplate(
    {
        domainName,
        basePackage,
        tablePackage,
        baseTable,
        refTables,
        tableInfo,
    }: {
        domainName: string,
        basePackage: string,
        tablePackage: string,
        baseTable: Table,
        refTables: RefTable[],
        tableInfo: { [k in string]: Table }
    }) {
    const ettBaseClassName = plural(domainName);
    const ettImplClassName = `${domainName}Entity`
    const repoClassName = `${domainName}Repository`
    const testRepoClassName = `${repoClassName}Test`

    const tableImport = [baseTable, ...refTables].map(table => `import ${tablePackage}.${pascal(table.name)}Table`)
    const recordImport = [baseTable, ...refTables].map(table => `import ${tablePackage}.${pascal(table.name)}Record`)
    const entityConstructorArgs = [
        ...baseTable.foreignColumns
            .map(col => `val ${camel(col.name)}: ${pascal(col.table)}ID`),
        ...baseTable.columns
            .map(col => `val ${camel(col.name)}: ${col.kType}`),
        ...refTables
            .map(sub => `${camel(sub.name)}Builder: ${ettBaseClassName}.(nextId: () -> ${pascal(sub.name)}ID) -> ${sub.many ? 'List<' : ''}${pascal(sub.name)}${sub.many ? '>' : ''}`)
    ];

    const arg = {
        domainName,
        basePackage,
        tablePackage,
        baseTable,
        refTables,
        tableImport,
        recordImport,
        entityConstructorArgs,
    }

    return {
        base: domainBaseTemplateKt({
            ...arg,
            className: ettBaseClassName,
            additionalImport: [
                `import ${basePackage}.impl.${ettImplClassName}`
            ]
        }),
        impl: domainImplTemplateKt({
            ...arg,
            className: ettImplClassName,
            additionalImport: [
                `import ${basePackage}.base.${ettBaseClassName}`
            ],
            baseClassName: ettBaseClassName,
        }),
        repo: domainRepoTmplKt({
            ...arg,
            className: repoClassName,
            additionalImport: [],
        }),
        testRepo: domainTestRepoTmplKt({
            ...arg,
            className: testRepoClassName,
            additionalImport: [],
            tableInfo,
            ettBaseClassName,
        }),
    };
}