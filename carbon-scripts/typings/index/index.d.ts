/** Global definitions for development **/

// Omit type https://github.com/Microsoft/TypeScript/issues/12215#issuecomment-377567046
type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>
type Dig<T, K extends keyof T> = T[K]
type Never<Key> = { [k in keyof Key]: never }
type PartialPick<T, K extends keyof T> = Partial<T> & Pick<T, K>

declare interface Array<T> {
    flat(): (T extends (infer U)[] ? U : never)[]

    flatMap<U>(f: (x: T) => U[]): U[]

    unique(): T[]
}

declare interface String {
    formatKt(): string

    indent(tab: number): string
}
