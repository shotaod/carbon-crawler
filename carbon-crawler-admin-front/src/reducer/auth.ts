import {Reducer} from 'redux'
import {initialState, State} from './state'
import {Action} from '../action'
import {LOCATION_CHANGE, LocationChangeAction} from "react-router-redux";

type AuthState = State.Auth
type AuthAction = Action.Auth.Actions | LocationChangeAction

type AuthReducer = Reducer<AuthState, AuthAction>
export const authReducer: AuthReducer = (state: AuthState = initialState.auth, action: AuthAction): AuthState => {
  const Types = Action.Auth.Types

  switch (action.type) {
    case LOCATION_CHANGE:
      return {
        ...state,
        errorMsg: undefined,
      }
    case Types.AUTH_SIGN_IN:
      return {
        ...state,
        errorMsg: undefined,
        signIn: false,
      }
    case Types.AUTH_SIGN_IN_SUCCESS:
      return {
        ...state,
        signIn: true,
      }
    case Types.AUTH_SIGN_OUT_SUCCESS:
      return {
        ...state,
        signIn: false,
      }
    case Types.AUTH_FORGOT_PASSWORD_SEND_CODE:
      return {
        ...state,
        errorMsg: undefined,
      }
    case Types.AUTH_ERROR:
      return {
        ...state,
        errorMsg: action.payload.errorMsg,
        signIn: false,
      }
    default:
      return state
  }
}
