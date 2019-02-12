import moment from "moment";

export function recordTemplateKt(
    {
        packages,
        imports,
        className,
        tableName,
        recordFields,
        columnFields,
    }: {
        packages: string,
        imports: string[],
        className: string,
        tableName: string,
        recordFields: string[],
        columnFields: string[],
    }) {
    return `${packages}

${imports.join('\n')}

/**
 * @author Soda(by carbon-scripts.gen.infra.ts) ${moment().format("LL.")}
 */
class ${className}Record(id: EntityID<String>) :
    AuditEntity<String>(id, ${className}Table),
    EagerTrait {
    
    companion object : EntityClass<String, ${className}Record>(${className}Table)
    
    ${recordFields.join('\n    ')}
}

object ${className}Table : AuditUUIDTable(name = "${tableName}") {
    ${columnFields.join('\n    ')}
} 
`
}