import {bindActionCreators, Dispatch} from 'redux'
import {connect} from 'react-redux'

import {compose, pure as renderOptimizeEffect} from 'recompose'

import {Action} from '../../action'
import {State} from '../../reducer/state'
import {QueryRegisterHandler, QueryRegisterView, QueryRegisterViewProps} from "../../component/page/query";

// ______________________________________________________
//
// @ I/F
type MappedState = {
  loading: boolean,
  errorMsg?: string,
  successMsg?: string,
}

// ______________________________________________________
//
// @ Connect
const mapState = (state: State.Root): MappedState => {
  const {loading, error, success} = state.remote.query.add
  const errorMsg = error && error.message
  const successMsg = (success && 'success register query') || undefined
  return {loading, errorMsg, successMsg};
}

const mapDispatch = (dispatch: Dispatch): QueryRegisterHandler => bindActionCreators<QueryRegisterHandler, QueryRegisterHandler>({
  handleRegister: (addForm: any) => new Action.Query.AddRequest(addForm).create()
}, dispatch)

const connectEffect = connect(
  mapState,
  mapDispatch,
)

const effect = compose<QueryRegisterViewProps, {}>(
  connectEffect,
  renderOptimizeEffect,
)

export const QueryAddContainer = effect(QueryRegisterView)
