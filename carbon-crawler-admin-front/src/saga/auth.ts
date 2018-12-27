import {call, fork, put, take} from 'redux-saga/effects'
import {push} from "react-router-redux"
import {Auth} from 'aws-amplify'

import {Action} from '../action'
import {routes} from "../route/routes"

export const authSagas = [watchSignUp, watchSignIn, watchSignOut, watchForgotPassword]

function* watchSignUp() {
  while (true) {
    const action: Action.Auth.SignUp = yield take(Action.Auth.Types.AUTH_SIGN_UP)
    const {email, password} = action.payload
    try {
      yield call({context: Auth, fn: Auth.signUp}, {username: email, password,})
      yield put(push(routes.auth.signUpResult))
    } catch (e) {
      yield put(new Action.Auth.Error(e.message).create())
    }
  }
}

type CognitoUser = any

function* watchSignIn() {
  while (true) {
    const action: Action.Auth.SignIn = yield take(Action.Auth.Types.AUTH_SIGN_IN)
    const {email, password} = action.payload
    try {
      const user: CognitoUser = yield call({context: Auth, fn: Auth.signIn}, email, password);
      if (user.challengeName === 'NEW_PASSWORD_REQUIRED') {
        // redirect change password page
        yield put(push(routes.auth.trouble.changePassword))
        yield fork(watchChangePassword, user)
        continue;
      }
      yield put(new Action.Auth.SignInSuccess().create())
      yield put(push(routes.home))
    } catch (e) {
      if (e.code === 'PasswordResetRequiredException') {
        yield put(push(routes.auth.trouble.forgetPassword))
        yield put(new Action.Auth.Error('you are requested to change password').create())
        continue;
      }
      yield put(new Action.Auth.Error(e.message).create())
    }
  }
}

function* watchSignOut() {
  while (true) {
    yield take(Action.Auth.Types.AUTH_SIGN_OUT)

    try {
      yield call({context: Auth, fn: Auth.signOut})
      yield put(new Action.Auth.SignOut().create())
      yield put(push(routes.auth.signIn))
    } catch (e) {
      yield put(new Action.Auth.Error(e.message).create())
    } finally {
    }
  }
}

function* watchChangePassword(user: CognitoUser) {
  while (true) {
    const action: Action.Auth.ChangePassword = yield take(Action.Auth.Types.AUTH_CHANGE_PASSWORD);
    const {password} = action.payload;

    try {
      yield call({context: Auth, fn: Auth.completeNewPassword}, user, password, {})
    } catch (e) {
      yield put(new Action.Auth.Error(e.message).create())
    } finally {
    }
  }
}

function* watchForgotPassword() {
  while (true) {
    const action: Action.Auth.ForgotPassword = yield take(Action.Auth.Types.AUTH_TROUBLE_FORGOT_PASSWORD)
    const {email} = action.payload;

    try {
      yield call({context: Auth, fn: Auth.forgotPassword}, email)
      yield put(new Action.Auth.ForgotPasswordSuccess().create())
      yield put(push(routes.auth.trouble.forgetPasswordConfirm))
    } catch (e) {
      yield put(new Action.Auth.Error(e.message).create())
    }
  }
}
