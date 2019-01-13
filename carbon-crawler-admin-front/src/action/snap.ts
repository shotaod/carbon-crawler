import {HttpMethod, Request} from '../service/api'
import * as SharedAction from './shared'

export class FetchRequest extends SharedAction.FsaActionCreator<Types.SNAP_FETCH_REQUEST, Request> {
  constructor(index: number, size: number) {
    super(Types.SNAP_FETCH_REQUEST, {
      method: HttpMethod.GET,
      path: '/v1/snaps',
      query: {page: index, size}
    })
  }
}

type FetchResponse = Fetch.Page<Fetch.SnapResponse>

export class FetchSuccess extends SharedAction.FsaActionCreator<Types.SNAP_FETCH_SUCCESS, FetchResponse> {
  constructor(response: FetchResponse) {
    super(Types.SNAP_FETCH_SUCCESS, response)
  }
}

export class FetchFailure extends SharedAction.FsaActionCreator<Types.SNAP_FETCH_FAILURE, { message: string }> {
  constructor(message: string) {
    super(Types.SNAP_FETCH_FAILURE, {message}, true)
  }
}

export type Actions =
  FetchRequest
  | FetchSuccess
  | FetchFailure

export enum Types {
  SNAP_FETCH_REQUEST = 'SNAP_FETCH_REQUEST',
  SNAP_FETCH_SUCCESS = 'SNAP_FETCH_SUCCESS',
  SNAP_FETCH_FAILURE = 'SNAP_FETCH_FAILURE',
}
