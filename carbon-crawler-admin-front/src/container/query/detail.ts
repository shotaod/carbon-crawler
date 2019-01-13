import {connect, Dispatch} from 'react-redux'

import {compose, pure as renderOptimizeEffect} from 'recompose'
import {Model, State} from '../../reducer/state'
import {QueryDetailView, QueryDetailViewProp, QueryUpdateHandler} from "../../component/page/query/";
import {Message} from "../../component/page/message";
import {bindActionCreators} from "redux";
import {Action} from "../../action";
import {queryParam} from "../helper";

// ______________________________________________________
//
// @ I/F
type MappedState = {
  loading: boolean,
  item: Model.Query,
} & Message

// ______________________________________________________
//
// @ Connect
const mapState = (state: State.Root): MappedState => {
  const {loading, error, success} = state.remote.query.put;
  const errorMsg = error && error.message
  const successMsg = (success && 'success update query') || undefined
  const {id} = queryParam(state, 'id');
  const item = state.query.items[parseInt(id!, 10)];
  if (!item) {
    // fetch one
  }
  return {
    loading,
    errorMsg,
    successMsg,
    item,
  };
}

const mapDispatch = (dispatch: Dispatch): QueryUpdateHandler => bindActionCreators<QueryUpdateHandler, QueryUpdateHandler>({
  handleUpdate: form => new Action.Query.PutRequest(form).create(),
}, dispatch)
const connectEffect = connect(
  mapState,
  mapDispatch,
)

const effect = compose<QueryDetailViewProp, {}>(
  connectEffect,
  renderOptimizeEffect,
)

export const QueryDetailContainer = effect(QueryDetailView)
