// ______________________________________________________
//
// @ Cardinality
import {sql} from "./sqls";

type RefererResult = {
    fromTableName: string
    fromColumnName: string
    toTableName: string
    toColumnNam: string
}

const referrerBaseQuery = (database: string) => `
  SELECT i.TABLE_NAME             as fromTableName,
         k.COLUMN_NAME            as fromColumnName,
         k.REFERENCED_TABLE_NAME  as toTableName,
         k.REFERENCED_COLUMN_NAME as toColumnName
  FROM information_schema.TABLE_CONSTRAINTS i
         LEFT JOIN information_schema.KEY_COLUMN_USAGE k ON i.CONSTRAINT_NAME = k.CONSTRAINT_NAME
  WHERE i.CONSTRAINT_TYPE = 'FOREIGN KEY'
    AND i.TABLE_SCHEMA = '${database}'
`

export class Cardinality {
    private readonly database: string

    constructor(database: string) {
        this.database = database;
    }

    selectParent = async (tableName: string) => {
        const {rows} = await sql`${referrerBaseQuery(this.database)} AND i.TABLE_NAME = '${tableName}'`;
        return rows.map(row => row as RefererResult);
    }
    selectChild = async (tableName: string) => {
        const {rows} = await sql`${referrerBaseQuery(this.database)} AND k.REFERENCED_TABLE_NAME = '${tableName}'`
        return rows.map(row => row as RefererResult);
    }
}
