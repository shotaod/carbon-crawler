// ______________________________________________________
//
// @ Util
export const pascal = (s: string) => camel(s.charAt(0).toUpperCase() + s.slice(1))
export const camel = (s: string) => s.replace(/(_\w)/g, match => match.charAt(1).toUpperCase());
