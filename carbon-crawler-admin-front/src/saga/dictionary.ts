import * as _ from 'lodash'
import {call, put, select, take} from 'redux-saga/effects'
import {Action} from '../action'
import {Api} from '../service/api'
import {State} from '../reducer/state'

function queryExistDictionary(state: State.Root, action: Action.Dictionary.FetchRequest) {
  return _.isEqual(state.dictionary.page, action.payload)
}

export function* watchLoadDictionary() {
  while (true) {
    const action: Action.Dictionary.FetchRequest = yield take(Action.Dictionary.Types.DICTIONARY_FETCH_REQUEST)
    const exist: boolean = yield select(queryExistDictionary, action)
    if (exist) continue

    const {response, error} = yield call(Api.call, action.payload)

    if (response) yield put(new Action.Dictionary.FetchSuccess(response).create())
    else yield put(new Action.Dictionary.FetchFailure(error).create())
  }
}

export function* watchAddDictionary() {
  while (true) {
    const action: Action.Dictionary.AddRequest = yield take(Action.Dictionary.Types.DICTIONARY_ADD_REQUEST)
    const {response, error} = yield call(Api.call, action.payload)

    if (response) yield put(new Action.Dictionary.AddSuccess(response).create())
    else yield put(new Action.Dictionary.AddFailure(error).create())
  }
}
