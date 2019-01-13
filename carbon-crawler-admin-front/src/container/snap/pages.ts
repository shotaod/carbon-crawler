import {compose, pure as renderOptimizeEffect, withHandlers} from 'recompose'
import {connect} from 'react-redux'
import {Model, State} from '../../reducer/state'
import {path} from "../../route/path";
import {SnapPageListView, SnapPageListViewProp} from "../../component/page/snap/pages";
import {queryParam} from "../helper";
import * as _ from "lodash";

// ______________________________________________________
//
// @ I/F
type Handler = Pick<SnapPageListViewProp, 'getPath'>
type MappedState = SnapPageListViewProp & { snapId: number }

// ______________________________________________________
//
// @ connect
const mapState = (state: State.Root): MappedState => {
  const {items} = state.snap
  const {id, pId} = queryParam(state, 'id', 'pId');
  const snapId = parseInt(id!, 10);
  const {pages} = items[snapId];

  let attributes: Model.SnapAttribute[] | undefined
  if (pId) {
    attributes = pages[parseInt(pId, 10)].attributes
  }
  return {
    snapId,
    attributes,
    items: _.values(pages),
    loading: false,
  }
}

const connectEffect = connect(
  mapState,
)

const detailHandlerEffect = withHandlers<MappedState, Handler>({
  getPath: ({snapId}) => id => `${path.snap.pages}?id=${snapId}&pId=${id}`
});

export const SnapPageListViewContainer = compose<SnapPageListViewProp, {}>(
  connectEffect,
  detailHandlerEffect,
  renderOptimizeEffect,
)(SnapPageListView)
