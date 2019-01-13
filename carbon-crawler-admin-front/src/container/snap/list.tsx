import * as _ from 'lodash'
import {compose, lifecycle, pure as renderOptimizeEffect, withHandlers, withProps} from 'recompose'
import {bindActionCreators, Dispatch} from 'redux'
import {connect} from 'react-redux'
import {Action} from '../../action'
import {State} from '../../reducer/state'
import {SnapListView, SnapListViewProp} from "../../component/page/snap";
import {path} from "../../route/path";

const PAGE_SIZE = 10

// ______________________________________________________
//
// @ I/F
type MappedDispatch = Pick<SnapListViewProp, 'handlePageClick'>
type Handler = Pick<SnapListViewProp, 'getPath'>
type MappedState = Omit<SnapListViewProp, keyof MappedDispatch | keyof Handler | 'header'>

// ______________________________________________________
//
// @ lifecycle
const lifecycleEffect = lifecycle<SnapListViewProp, {}>({
  componentWillMount() {
    const {index = 0} = this.props
    this.props.handlePageClick(index)
  },
})

// ______________________________________________________
//
// @ connect
const mapState = (state: State.Root): MappedState => {
  const {items, page} = state.snap
  const {loading, error} = state.remote.snap.fetch
  const errorMsg = error && error.message
  const _items = _.values(items)
  return {
    errorMsg,
    loading,
    ...page,
    items: _items,
  }
}

const mapDispatch = (dispatch: Dispatch) => bindActionCreators<MappedDispatch, MappedDispatch>({
  handlePageClick: index => new Action.Snap.FetchRequest(index, PAGE_SIZE).create(),
}, dispatch)

const connectEffect = connect(
  mapState,
  mapDispatch,
)

const propsEffect = withProps<Pick<SnapListViewProp, 'header'>, never>({
  header: ['id', 'title', 'url'],
});

const detailHandlerEffect = withHandlers<{}, Handler>({
  getPath: () => id => `${path.snap.pages}?id=${id}`
});

export const SnapListViewContainer = compose<SnapListViewProp, {}>(
  propsEffect,
  connectEffect,
  detailHandlerEffect,
  lifecycleEffect,
  renderOptimizeEffect,
)(SnapListView)
