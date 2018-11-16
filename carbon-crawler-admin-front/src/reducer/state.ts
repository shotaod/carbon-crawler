import {RouterState} from 'react-router-redux'
import {correct} from '../utils'
import {Page} from '../shared'

export namespace State {

  import CallState = Common.CallState;

  export type Root = {
    auth: Auth
    remote: Remote<CallState>
    dictionary: Dictionary
    router: RouterState
  }

  export namespace Common {
    export type CallState = {
      loading: boolean
    }
  }

  export type Auth = {
    signIn: boolean
  }

  export type Remote<V> = {
    dictionary: {
      [key in DictionaryRemotes]: V
      }
  }

  type DictionaryRemotes = 'fetch' | 'add'
  export type Dictionary = {
    data: {
      [key in number]: Model.Dictionary
      }
    page?: Page
  }
}

export namespace Model {
  export interface Dictionary {
    id: number
    url: string
    title: string
    memo: string
  }
}

export const initialState: State.Root = {
  auth: {
    signIn: false,
  },
  remote: {
    dictionary: correct({loading: false}, 'fetch', 'add')
  },
  dictionary: {
    data: {},
  },
  router: {location: null},
}
