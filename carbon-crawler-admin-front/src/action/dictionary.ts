import {HttpMethod, Request} from '../service/api'
import {Page} from '../shared'
import * as SharedAction from './shared'

export class FetchRequest extends SharedAction.FsaActionCreator<Types.DICTIONARY_FETCH_REQUEST, Request> {
  constructor(index: number, size: number) {
    super(Types.DICTIONARY_FETCH_REQUEST, {
      method: HttpMethod.GET,
      path: '/v1/dictionaries',
      query: {page: index, size}
    })
  }
}

type FetchResponse = {
  items: {
    id: number
    title: string,
    url: string,
    memo?: string,
  }[],
  page: Page
}

export class FetchSuccess extends SharedAction.FsaActionCreator<Types.DICTIONARY_FETCH_SUCCESS, FetchResponse> {
  constructor(response: FetchResponse) {
    super(Types.DICTIONARY_FETCH_SUCCESS, response)
  }
}

export class FetchFailure extends SharedAction.FsaActionCreator<Types.DICTIONARY_FETCH_FAILURE, { message: string }> {
  constructor(message: string) {
    super(Types.DICTIONARY_FETCH_FAILURE, {message}, true)
  }
}

export type AddForm = { url: string, title: string, memo?: string }

export class AddRequest extends SharedAction.FsaActionCreator<Types.DICTIONARY_ADD_REQUEST, Request<AddForm>> {
  constructor(form: AddForm) {
    super(Types.DICTIONARY_ADD_REQUEST, {method: HttpMethod.POST, path: '/v1/dictionaries', body: form})
  }
}

export class AddSuccess extends SharedAction.FsaActionCreator<Types.DICTIONARY_ADD_SUCCESS, {}> {
  constructor(response: any) {
    super(Types.DICTIONARY_ADD_SUCCESS, {response})
  }
}

export class AddFailure extends SharedAction.FsaActionCreator<Types.DICTIONARY_ADD_FAILURE, { message: string }> {
  constructor(message: string) {
    super(Types.DICTIONARY_ADD_FAILURE, {message}, true)
  }
}

export type Actions = FetchRequest | FetchSuccess | FetchFailure | AddRequest | AddSuccess | AddFailure

export enum Types {
  DICTIONARY_FETCH_REQUEST = 'DICTIONARY_FETCH_REQUEST',
  DICTIONARY_FETCH_SUCCESS = 'DICTIONARY_FETCH_SUCCESS',
  DICTIONARY_FETCH_FAILURE = 'DICTIONARY_FETCH_FAILURE',
  DICTIONARY_ADD_REQUEST = 'DICTIONARY_ADD_REQUEST',
  DICTIONARY_ADD_SUCCESS = 'DICTIONARY_ADD_SUCCESS',
  DICTIONARY_ADD_FAILURE = 'DICTIONARY_ADD_FAILURE',
}
