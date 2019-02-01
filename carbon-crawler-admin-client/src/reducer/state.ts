import {RouterState} from 'react-router-redux'
import {fillValue} from '../utils'

export namespace State {
  import CallState = Common.CallState;
  import Query = Model.Query;
  import Paging = Common.Paging;
  import Snap = Model.Snap;

  export type Root = {
    remote: Remote<CallState>
    router: RouterState
    auth: Auth
    query: Paging<Query>
    snap: Paging<Snap>
  }

  export namespace Common {
    export type CallState = {
      loading: boolean
      error?: {
        message: string,
      },
      success?: boolean,
    }
    export type Page = {
      index: number,
      size: number,
      total: number,
    }
    export type Paging<T> = {
      items: T
      page: Page
    }
  }

  // ______________________________________________________
  //
  // @ remotes
  export type Remote<R> = {
    query: {
      [key in QueryRemotes]: R
    }
    snap: {
      [key in SnapRemotes]: R
    }
  }
  type QueryRemotes = 'fetch' | 'add' | 'put'
  type SnapRemotes = 'fetch'

  // ______________________________________________________
  //
  // @ auth
  export type Auth = {
    errorMsg?: string
    trouble?: AuthForgotPassword
  }
  export type AuthForgotPassword = {
    forgot: {
      email?: string,
      emailSend: boolean,
      code?: string,
      newPassword?: string,
    }
  }
}

export namespace Model {
  // ______________________________________________________
  //
  // @ Shared
  import Normalized = Model.Shared.Normalized;
  export namespace Shared {
    export type Normalized<T> = {
      [key in number]: T
    }
    export type Unwrap<T extends Normalized<any>> = T extends Normalized<infer U> ? U : never
  }

  // ______________________________________________________
  //
  // @ Host
  export type Host = {
    id: number,
    url: string,
    title: string,
    memo: string | undefined,
  }

  // ______________________________________________________
  //
  // @ Query
  export type Query = Normalized<{
    listing: {
      pagePath: string,
      linkQuery: string,
    }
    details: {
      queryName: string,
      query: string,
      type: string,
    }[]
  } & Host>

  // ______________________________________________________
  //
  // @ Snap
  export type SnapAttribute = {
    key: string,
    value: string,
    type: string, //'text/text' | 'image/text',
  }
  export type Snap = Normalized<{
    pages: Normalized<{
      id: number,
      title: string,
      url: string,
      attributes: SnapAttribute[]
    }>
  } & Host>
}

const page: State.Common.Page = {
  index: 0,
  size: 10,
  total: 10,
}

export const initialState: State.Root = {
  remote: {
    query: fillValue({loading: false, error: undefined, success: undefined}, 'fetch', 'add', 'put'),
    snap: fillValue({loading: false, error: undefined, success: undefined}, 'fetch'),
  },
  router: {
    location: null
  },
  auth: {},
  query: {
    items: {},
    page,
  },
  snap: {
    items: {},
    page,
  }
}
