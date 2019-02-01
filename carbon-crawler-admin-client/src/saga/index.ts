import {all, fork} from 'redux-saga/effects'
import {querySagas} from './query'
import {authSagas} from './auth'
import {snapSagas} from "./snap";


export function* rootSaga() {
  yield all([
    ...authSagas.map(fork),
    ...querySagas.map(fork),
    ...snapSagas.map(fork),
  ])
}
