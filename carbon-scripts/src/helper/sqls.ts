// ______________________________________________________
//
// @ mysql
import * as mysql from 'mysql';
import {FieldInfo, MysqlError} from 'mysql';
import * as util from "util";

let conn: mysql.Connection | undefined;

export namespace MySQL {
    export async function run(config: mysql.ConnectionConfig, block: (conn: mysql.Connection) => Promise<void>) {
        conn = mysql.createConnection(config);
        try {
            conn.connect()

            await block(conn);

        } finally {
            conn.end()
        }
    }
}

function wrapQuery(query: string, cb: (err: MysqlError | null, res: { result: any, fields: FieldInfo[] | undefined }) => void) {
    conn!!.query(query, (err, result, fields) => cb(err, {result, fields}));
}

const promisql = util.promisify(wrapQuery);

export async function sql<T = any>(query: TemplateStringsArray, ...args: any[]) {
    const qStr = query.map(qNode => {
        if (args.length === 0) return qNode;
        return qNode + args.shift();
    }).join('');


    console.log('[sql]', qStr)

    const {fields, result} = await promisql(qStr);
    if (!result || !fields) throw new Error(`cannot fetch data.
    fields: ${fields}
    result: ${result}
`);
    return {rows: result as T[], fields};
}