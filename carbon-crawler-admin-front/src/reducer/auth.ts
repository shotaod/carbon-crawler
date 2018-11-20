import {Reducer} from 'redux'
import {initialState, State} from './state'
import {Action} from '../action'

type AuthState = State.Auth
type AuthAction = Action.Auth.Actions
type AuthReducer = Reducer<AuthState, AuthAction>
export const authReducer: AuthReducer = (state: AuthState = initialState.auth, action: AuthAction): AuthState => {
  const Types = Action.Auth.Types

  switch (action.type) {
    case Types.AUTH_SIGN_IN_SUCCESS:
      return {
        signIn: true,
      }
    case Types.AUTH_SIGN_OUT_SUCCESS:
      return {
        signIn: false,
      }
    case Types.AUTH_ERROR:
      return {
        errorMsg: action.payload.errorMsg,
        signIn: false,
      }
    default:
      return state;
  }
}
