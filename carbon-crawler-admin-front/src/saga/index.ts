import {all, fork} from 'redux-saga/effects'
import {watchAddDictionary, watchLoadDictionary} from './dictionary'

export function* rootSaga() {
  yield all([
    fork(watchLoadDictionary),
    fork(watchAddDictionary)
  ])
}
