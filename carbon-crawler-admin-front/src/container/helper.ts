import * as url from "url";
import {State} from "../reducer/state";

export function queryParam<K extends string>(state: State.Root, ...path: K[]) {
  if (!state.router.location) throw new Error('illegal state')
  const {query} = url.parse(state.router.location.search, true)
  return path
    .map(p => ({
      key: p,
      value: query[p],
    }))
    .reduce((acc, {key, value}) => {
      acc[key] = value
      return acc
    }, {} as any) as { [T in K]?: string }
}
