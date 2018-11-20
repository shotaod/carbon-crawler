import {all, fork} from 'redux-saga/effects'
import {watchAddDictionary, watchLoadDictionary} from './dictionary'
import {authSagas} from './auth'


export function* rootSaga() {
  yield all([
    fork(watchLoadDictionary),
    fork(watchAddDictionary),
    ...authSagas.map(fork),
  ])
}
