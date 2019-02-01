import {call, fork, put, take} from 'redux-saga/effects'
import {push} from "react-router-redux"
import Auth from '@aws-amplify/auth'
import {CognitoUser as BaseCognitoUser} from 'amazon-cognito-identity-js'
import {Action} from '../action'
import {path} from "../route/path"

export const authSagas = [
  watchSignUp,
  watchSignIn,
  watchSignOut,
  watchChangePassword,
  watchForgotPasswordSendCode,
]

function* watchSignUp() {
  while (1) {
    try {
      const action: Action.Auth.SignUp = yield take(Action.Auth.Types.AUTH_SIGN_UP)
      const {email, password} = action.payload
      yield call({context: Auth, fn: Auth.signUp}, {username: email, password})
      yield put(push(path.auth.signUpResult))
    } catch (e) {
      yield put(new Action.Auth.Error(e.message).create())
    }
  }
}

type CognitoUser = BaseCognitoUser | any

function* watchSignIn() {
  while (true) {
    try {
      const action: Action.Auth.SignIn = yield take(Action.Auth.Types.AUTH_SIGN_IN)
      const {email, password} = action.payload

      const user: CognitoUser = yield call({context: Auth, fn: Auth.signIn}, email, password)
      if (user.challengeName === 'NEW_PASSWORD_REQUIRED') {
        // redirect change password page
        yield put(push(path.auth.trouble.changePassword))
        continue
      }
      yield put(new Action.Auth.SignInSuccess().create())
      yield put(push(path.home))
    } catch (e) {
      if (e.code === 'PasswordResetRequiredException') {
        // redirect reset password page
        yield put(push(path.auth.trouble.forgotPassword))
        yield put(new Action.Auth.Error('you are requested to reset password').create())
        continue
      }
      yield put(new Action.Auth.Error(e.message).create())
    }
  }
}

function* watchSignOut() {
  while (true) {
    try {
      yield take(Action.Auth.Types.AUTH_SIGN_OUT)
      yield call({context: Auth, fn: Auth.signOut})
      yield put(new Action.Auth.SignOut().create())
      yield put(push(path.auth.signIn))
    } catch (e) {
      yield put(new Action.Auth.Error(e.message).create())
    } finally {
    }
  }
}

function* watchChangePassword(user: CognitoUser) {
  while (1) {
    try {
      const action: Action.Auth.ChangePassword = yield take(Action.Auth.Types.AUTH_CHANGE_PASSWORD)
      const {password} = action.payload
      yield call({context: Auth, fn: Auth.completeNewPassword}, user, password, {})
    } catch (e) {
      yield put(new Action.Auth.Error(e.message).create())
    }
  }
}

function* watchForgotPasswordSendCode() {

  function* flowForgotPasswordRenew() {
    try {
      const action: Action.Auth.ForgotPasswordRenew = yield take(Action.Auth.Types.AUTH_FORGOT_PASSWORD_RENEW)
      const {email, code, password} = action.payload
      yield call({context: Auth, fn: Auth.forgotPasswordSubmit}, email, code, password)
    } catch (e) {
      yield put(new Action.Auth.Error(e.message).create())
    }
  }

  while (true) {
    try {
      const action: Action.Auth.ForgotPasswordSendCode = yield take(Action.Auth.Types.AUTH_FORGOT_PASSWORD_SEND_CODE)
      const {email} = action.payload;
      yield call({context: Auth, fn: Auth.forgotPassword}, email)
      fork(flowForgotPasswordRenew)
    } catch (e) {
      yield put(new Action.Auth.Error(e.message).create())
    }
  }
}
