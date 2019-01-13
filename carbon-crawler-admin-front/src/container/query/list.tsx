import * as _ from 'lodash'
import {compose, lifecycle, pure as renderOptimizeEffect, withHandlers, withProps} from 'recompose'
import {bindActionCreators, Dispatch} from 'redux'
import {connect} from 'react-redux'
import {Action} from '../../action'
import {State} from '../../reducer/state'
import {QueryListView, QueryListViewProp} from "../../component/page/query";
import {path} from "../../route/path";

const PAGE_SIZE = 10

// ______________________________________________________
//
// @ I/F
type MappedDispatch = Pick<QueryListViewProp, 'handlePageClick'>
type Handler = Pick<QueryListViewProp, 'getPath'>
type MappedState = Omit<QueryListViewProp, keyof MappedDispatch | keyof Handler | 'header'>

// ______________________________________________________
//
// @ lifecycle
const lifecycleEffect = lifecycle<QueryListViewProp, {}>({
  componentWillMount() {
    const {index = 0} = this.props
    this.props.handlePageClick(index)
  },
})

// ______________________________________________________
//
// @ connect
const mapState = (state: State.Root): MappedState => {
  const {items, page} = state.query
  const {loading, error} = state.remote.query.fetch
  const errorMsg = error && error.message
  const _items = _.values(items)
    .map(({id, title, url}) => ({id, title, url}))
  return {
    errorMsg,
    loading,
    ...page,
    items: _items,
  }
}

const mapDispatch = (dispatch: Dispatch) => bindActionCreators<MappedDispatch, MappedDispatch>({
  handlePageClick: index => new Action.Query.FetchRequest(index, PAGE_SIZE).create(),
}, dispatch)

const connectEffect = connect(
  mapState,
  mapDispatch,
)

const propsEffect = withProps<Pick<QueryListViewProp, 'header'>, never>({
  header: ['id', 'title', 'url'],
});

const detailHandlerEffect = withHandlers<{}, Handler>({
  getPath: () => id => `${path.query.detail}?id=${id}`
});

export const QueryTableContainer = compose<QueryListViewProp, {}>(
  propsEffect,
  connectEffect,
  detailHandlerEffect,
  lifecycleEffect,
  renderOptimizeEffect,
)(QueryListView)
