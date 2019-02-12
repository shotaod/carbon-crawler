import moment from "moment";
import {camel, pascal} from "../../helper/strings";
import {TemplateArgs} from "./index";

export function domainBaseTemplateKt(
    {
        className,
        basePackage,
        tablePackage,
        baseTable,
        refTables,
        tableImport,
        additionalImport,
        entityConstructorArgs
    }: TemplateArgs) {
    return `package ${basePackage}.base

${[
        `import ${basePackage}.shared.Entity`,
        `import ${basePackage}.shared.EntityID`,
        `import ${basePackage}.shared.IDGenerator`,
        ...additionalImport,
        ...tableImport,
    ].filter(Boolean).unique().sort().join('\n')}

/**
 * @author Soda(by carbon-scripts.gen.domain.entity.base) ${moment().format("LL.")}
 */
// ______________________________________________________
//
// @ ID
${[baseTable, ...refTables].map(({name}) => `class ${pascal(name)}ID(value: String) : EntityID(value, ${pascal(name)}Table)`).join('\n')}

// ______________________________________________________
//
// @ Domain Entity
abstract class ${className} internal constructor(
    ${[
        `override val id: ${pascal(baseTable.name)}ID`,
        ...entityConstructorArgs]
        .join(',\n    ')}
) : Entity<${pascal(baseTable.name)}ID> {
    ${refTables.map(sub =>
        `inner class ${pascal(sub.name)} constructor(
        ${[
            `val id: ${pascal(sub.name)}ID`,
            ...sub.columns.map(col => `val ${camel(col.name)}: ${col.kType}`)]
            .join(',\n        ')}
    )`).join('\n    ')}

${refTables.map(sub => `
    @Suppress("LeakingThis")
    val ${camel(sub.name)} = ${camel(sub.name)}Builder(this) { ${pascal(sub.name)}ID(IDGenerator.next()) }`).join('').slice(1)}

    companion object Factory {
        fun new(
            ${entityConstructorArgs.join(',\n            ').replace(/val /g, '')}
        ) = ${pascal(baseTable.name)}Entity(
            ${[
        `${pascal(baseTable.name)}ID(IDGenerator.next())`,
        ...entityConstructorArgs].join(',\n            ').replace(/val /g, '').replace(/:[^,]+(,?)/g, '$1')}
        )
    }
}
`.formatKt()
}


