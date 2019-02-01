import * as Query from './query'

export type Actions = Query.Actions
export const Types = {
  ...Query.Types
}

export const REMOTE_RESET = 'REMOTE_RESET'
export const resetRemote = {
  type: REMOTE_RESET,
  payload: {},
}
