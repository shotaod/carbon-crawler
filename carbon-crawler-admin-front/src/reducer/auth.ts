import {Reducer} from 'redux'
import {initialState, State} from './state'
import {Action} from '../action'

type AuthState = State.Auth
type AuthAction = Action.Auth.Actions
type AuthReducer = Reducer<AuthState, AuthAction>
export const authReducer: AuthReducer = (state: AuthState = initialState.auth, action: AuthAction): AuthState => {
  const Types = Action.Auth.Types

  switch (action.type) {
    case Types.AUTH_LOGIN:
      return {
        login: true,
      }
    case Types.AUTH_LOGOUT:
      return {
        login: false,
      }
    default:
      return state
  }
}
