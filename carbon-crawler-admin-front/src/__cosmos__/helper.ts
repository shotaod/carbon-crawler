export const withName = <C>(n: string) => (c: C): C => {
  (c as any).displayName = n
  return c;
}
