import * as _ from 'lodash'
import {call, put, select, take} from 'redux-saga/effects'
import {Action} from '../action'
import {Api} from '../service/api'
import {State} from '../reducer/state'

export const snapSagas = [
  watchLoadSnap,
]

function* watchLoadSnap() {
  while (true) {
    const action: Action.Snap.FetchRequest = yield take(Action.Snap.Types.SNAP_FETCH_REQUEST)
    const exist: boolean = yield select(queryExistSnap, action)
    if (exist) continue

    const {result, error} = yield call(Api.call, action.payload)
    if (result) yield put(new Action.Snap.FetchSuccess(result).create())
    else yield put(new Action.Snap.FetchFailure(error.message).create())
  }
}

// ______________________________________________________
//
// @ private
function queryExistSnap(state: State.Root, action: Action.Snap.FetchRequest) {
  return _.isEqual(state.snap.page, action.payload)
}
