export function omit<T extends object, K extends keyof T>(target: T, ...omitKeys: K[]): Omit<T, K> {
  return (Object.keys(target) as K[]).reduce(
    (res, key) => {
      if (!omitKeys.includes(key)) {
        res[key] = target[key]
      }
      return res
    },
    {} as any
  )
}

export const correct = <k extends string, v extends any>(initialValue: v, ...keys: k[]): { [Key in k]: v } => {
  return keys.reduce((acc, k) => Object.assign({}, acc, {[k]: initialValue}), {}) as { [Key in k]: v }
}

export const render = (element: JSX.Element | JSX.Element[] | (() => JSX.Element) | null): JSX.Element | JSX.Element[] | null => {
  if (!element) return null

  if (typeof element === 'function') {
    return element()
  }

  return element
}
