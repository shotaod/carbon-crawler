import moment from "moment";
import {TemplateArgs} from "./index";
import {pascal} from "../../helper/strings";

export function domainImplTemplateKt(
    {
        className,
        baseTable,
        baseClassName,
        basePackage,
        refTables,
        additionalImport,
        entityConstructorArgs,
    }: TemplateArgs & {
        baseClassName: string,
    }
) {
    const idTables = [baseTable, ...baseTable.foreignColumns.map(({table}) => ({name: table}))].sort();
    const imports = [
        ...idTables.map(({name}) => `import ${basePackage}.base.${pascal(name)}ID`),
        ...refTables.map(ref => `import ${basePackage}.base.${pascal(ref.name)}ID`),
        ...additionalImport,
    ].unique().sort();
    return `package ${basePackage}.impl.stub

${imports.join('\n')}

/**
 * @author Soda(by carbon-scripts.gen.domain.entity.impl) ${moment().format("LL.")}
 */
class ${className} internal constructor(
    ${[
        `id: ${pascal(baseTable.name)}ID`,
        ...entityConstructorArgs
    ].join(',\n    ').replace(/val /g, '')}
) : ${baseClassName}(
    ${[
        'id',
        ...entityConstructorArgs
    ].join(',\n    ').replace(/val /g, '').replace(/(:[^,]+)(,?)/g, '$2')}
) {
    // ______________________________________________________
    //
    // @ Entity Logic Implementation
}
`.formatKt()
}
