import * as util from "util";
import * as fs from "fs";
import * as yaml from 'js-yaml';

export const asyncWriteFile = util.promisify(fs.writeFile);
export const asyncReadFile = util.promisify(fs.readFile);
export const asyncMakeDir = util.promisify(fs.mkdir);
export const asyncReadDir = util.promisify(fs.readdir);
export const asyncUnLink = util.promisify(fs.unlink);

export async function asyncReadYaml<T = any>(path: string) {
    const file = await asyncReadFile(path, 'utf-8');
    return yaml.safeLoad(file) as T
}