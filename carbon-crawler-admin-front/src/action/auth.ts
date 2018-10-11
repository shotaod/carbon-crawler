import * as SharedAction from './shared'

export class AuthLogin extends SharedAction.FsaActionCreator<Types.AUTH_LOGIN, {}> {
  constructor() {
    super(Types.AUTH_LOGIN, {})
  }
}

export class AuthLogout extends SharedAction.FsaActionCreator<Types.AUTH_LOGOUT, {}> {
  constructor() {
    super(Types.AUTH_LOGOUT, {})
  }
}

export type Actions = AuthLogin | AuthLogout

export enum Types {
  AUTH_LOGIN = 'AUTH_LOGIN',
  AUTH_LOGOUT = 'AUTH_LOGOUT',
}
