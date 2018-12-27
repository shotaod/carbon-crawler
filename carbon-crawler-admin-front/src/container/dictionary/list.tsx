import * as _ from 'lodash'
import {compose, lifecycle, pure as renderOptimizeEffect} from 'recompose'
import {bindActionCreators, Dispatch} from 'redux'
import {connect} from 'react-redux'
import {Action} from '../../action'
import {Model, State} from '../../reducer/state'
import {ListView} from "../../component/dictionary/ListView";
import {Page} from "../../shared";

const PAGE_SIZE = 10

// ______________________________________________________
//
// @ I/F
type Props = MappedState & MappedDispatch

type MappedState = {
  errorMsg?: string
  empty: boolean
  items: Model.Dictionary[]
  page?: Page
  loading: boolean
}

type MappedDispatch = {
  dispatchFetchDictionary: (index: number) => void
}

// ______________________________________________________
//
// @ lifecycle
const lifecycleEffect = lifecycle<Props, {}>({
  componentWillMount() {
    const page = this.props.page
    const index = page ? page.index : 0
    this.props.dispatchFetchDictionary(index)
  },
})

// ______________________________________________________
//
// @ connect
const mapState = (state: State.Root): MappedState => {
  const {data, page} = state.dictionary
  const {loading, error} = state.remote.dictionary.fetch
  const errorMsg = error && error.message
  if (!(data && page)) {
    return {
      empty: true,
      items: [],
      loading,
      errorMsg,
    }
  }

  const items = _.values(data)
  return {
    empty: false,
    items,
    page,
    loading,
  }
}

const mapDispatch = (dispatch: Dispatch): MappedDispatch =>
  bindActionCreators({
    dispatchFetchDictionary: (index: number) => new Action.Dictionary.FetchRequest(index, PAGE_SIZE).create()
  }, dispatch)

const connectEffect = connect(
  (store: State.Root) => mapState(store),
  (dispatch: Dispatch) => mapDispatch(dispatch)
)

const effect = compose<Props, {}>(
  connectEffect,
  renderOptimizeEffect,
  lifecycleEffect,
)

export const DictionaryListContainer = effect(ListView)
