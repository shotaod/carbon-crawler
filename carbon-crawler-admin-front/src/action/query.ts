import {HttpMethod, Request} from '../service/api'
import * as SharedAction from './shared'

export class FetchRequest extends SharedAction.FsaActionCreator<Types.QUERY_FETCH_REQUEST, Request> {
  constructor(index: number, size: number) {
    super(Types.QUERY_FETCH_REQUEST, {
      method: HttpMethod.GET,
      path: '/v1/queries',
      query: {page: index, size}
    })
  }
}

type FetchResponse = Fetch.Page<Fetch.QueryResponse>
type FetchAddRequest = Fetch.QueryAddRequest

export class FetchSuccess extends SharedAction.FsaActionCreator<Types.QUERY_FETCH_SUCCESS, FetchResponse> {
  constructor(response: FetchResponse) {
    super(Types.QUERY_FETCH_SUCCESS, response)
  }
}

export class FetchFailure extends SharedAction.FsaActionCreator<Types.QUERY_FETCH_FAILURE, { message: string }> {
  constructor(message: string) {
    super(Types.QUERY_FETCH_FAILURE, {message}, true)
  }
}

export class AddRequest extends SharedAction.FsaActionCreator<Types.QUERY_ADD_REQUEST, Request<FetchAddRequest>> {
  constructor(form: FetchAddRequest) {
    super(Types.QUERY_ADD_REQUEST, {
      method: HttpMethod.POST,
      path: '/v1/queries',
      body: form
    })
  }
}

export class AddSuccess extends SharedAction.FsaActionCreator<Types.QUERY_ADD_SUCCESS, {}> {
  constructor(response: any) {
    super(Types.QUERY_ADD_SUCCESS, {response})
  }
}

export class AddFailure extends SharedAction.FsaActionCreator<Types.QUERY_ADD_FAILURE, { message: string }> {
  constructor(message: string) {
    super(Types.QUERY_ADD_FAILURE, {message}, true)
  }
}

export class PutRequest extends SharedAction.FsaActionCreator<Types.QUERY_PUT_REQUEST, Request<FetchAddRequest>> {
  constructor(form: FetchAddRequest) {
    super(Types.QUERY_PUT_REQUEST, {
      method: HttpMethod.PUT,
      path: `/v1/queries/${form.id}`,
      body: form
    })
  }
}

export class PutSuccess extends SharedAction.FsaActionCreator<Types.QUERY_PUT_SUCCESS, {}> {
  constructor(response: any) {
    super(Types.QUERY_PUT_SUCCESS, {response})
  }
}

export class PutFailure extends SharedAction.FsaActionCreator<Types.QUERY_PUT_FAILURE, { message: string }> {
  constructor(message: string) {
    super(Types.QUERY_PUT_FAILURE, {message}, true)
  }
}

export type Actions =
  FetchRequest
  | FetchSuccess
  | FetchFailure
  | AddRequest
  | AddSuccess
  | AddFailure
  | PutRequest
  | PutSuccess
  | PutFailure

export enum Types {
  QUERY_FETCH_REQUEST = 'QUERY_FETCH_REQUEST',
  QUERY_FETCH_SUCCESS = 'QUERY_FETCH_SUCCESS',
  QUERY_FETCH_FAILURE = 'QUERY_FETCH_FAILURE',
  QUERY_ADD_REQUEST = 'QUERY_ADD_REQUEST',
  QUERY_ADD_SUCCESS = 'QUERY_ADD_SUCCESS',
  QUERY_ADD_FAILURE = 'QUERY_ADD_FAILURE',
  QUERY_PUT_REQUEST = 'QUERY_PUT_REQUEST',
  QUERY_PUT_SUCCESS = 'QUERY_PUT_SUCCESS',
  QUERY_PUT_FAILURE = 'QUERY_PUT_FAILURE',
}
