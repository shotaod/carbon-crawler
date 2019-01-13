import * as _ from 'lodash'
import {call, put, select, take} from 'redux-saga/effects'
import {Action} from '../action'
import {Api} from '../service/api'
import {State} from '../reducer/state'

function queryExistQuery(state: State.Root, action: Action.Query.FetchRequest) {
  return _.isEqual(state.query.page, action.payload)
}

export const querySagas = [
  watchLoadQuery,
  watchAddQuery,
  watchPutQuery,
]

function* watchLoadQuery() {
  while (true) {
    const action: Action.Query.FetchRequest = yield take(Action.Query.Types.QUERY_FETCH_REQUEST)
    const exist: boolean = yield select(queryExistQuery, action)
    if (exist) continue

    const {result, error} = yield call(Api.call, action.payload)

    if (result) yield put(new Action.Query.FetchSuccess(result).create())
    else yield put(new Action.Query.FetchFailure(error.message).create())
  }
}

function* watchAddQuery() {
  while (true) {
    const action: Action.Query.AddRequest = yield take(Action.Query.Types.QUERY_ADD_REQUEST)
    const {result, error} = yield call(Api.call, action.payload)
    if (result) yield put(new Action.Query.AddSuccess(result).create())
    else yield put(new Action.Query.AddFailure(error.message).create())
  }
}

function* watchPutQuery() {
  while (1) {
    const action: Action.Query.PutRequest = yield take(Action.Query.Types.QUERY_PUT_REQUEST);
    const {result, error} = yield call(Api.call, action.payload);
    if (result) yield put(new Action.Query.PutSuccess(result).create())
    else yield put(new Action.Query.PutFailure(error.message).create())
  }
}
