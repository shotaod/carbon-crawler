import {call, put, take} from 'redux-saga/effects'
import {push} from "react-router-redux";
import {Auth} from 'aws-amplify'

import {Action} from '../action'
import {routes} from "../route/routes";

export const authSagas = [watchSignUp, watchSignIn, watchSignOut]

function* watchSignUp() {
  while (true) {
    const action: Action.Auth.SignUp = yield take(Action.Auth.Types.AUTH_SIGN_UP)
    const {email, password} = action.payload;
    try {
      yield call({context: Auth, fn: Auth.signUp}, {username: email, password,});
      yield put(push(routes.auth.signUpResult))
    } catch (e) {
      console.error(e)
      yield put(new Action.Auth.Error(e.message).create())
    }
  }
}

function* watchSignIn() {
  while (true) {
    const action: Action.Auth.SignIn = yield take(Action.Auth.Types.AUTH_SIGN_IN);
    const {email, password} = action.payload
    try {
      yield call({context: Auth, fn: Auth.signIn}, email, password)
      yield put(push(routes.home))
    } catch (e) {
      console.error(e)
      yield put(new Action.Auth.Error(e.message).create())
    }
  }
}

function* watchSignOut() {
  while (true) {
    yield take(Action.Auth.Types.AUTH_SIGN_OUT);

    try {
      yield call({context: Auth, fn: Auth.signOut});
      yield put(new Action.Auth.SignOut().create())
      yield put(push(routes.auth.signIn))
    } catch (e) {
      yield put(new Action.Auth.Error(e).create())
    }
  }
}
