import {camel} from "./strings";

type TypeMappings = { [k in string]: string }


const kDBTypeMappings: TypeMappings = {
    char: 'varchar'
}

const kEttTypeMappings: TypeMappings = {
    char: 'String',
    varchar: 'String',
    text: 'String',
}

const parseDBType = (dbType: string) => {
    let param: string | undefined
    const type = camel(dbType.replace(/\(\d+\)/, match => {
        param = match.replace(/^\((\d+)\)$/, '$1');
        return '';
    }))
    return {
        type,
        param,
    }
}
export const mapDBType = (dbType: string, nullable: boolean) => {
    return {
        ...parseDBType(dbType),
        nullable
    }
}

export const mapKDBType = (dbType: string) => {
    const {type, param} = parseDBType(dbType);

    return {
        type: kDBTypeMappings[type] || type,
        param,
    }
};


export const mapKType = (dbType: string, nullable: boolean) => {
    const {type} = parseDBType(dbType)
    const ettType = kEttTypeMappings[type];
    if (!ettType) throw new Error(`!type not registered! dbtype: ${dbType}`)

    return `${ettType}${nullable ? '? = null' : ''}`
}