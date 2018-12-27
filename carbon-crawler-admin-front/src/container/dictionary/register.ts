import {bindActionCreators, Dispatch} from 'redux'
import {connect} from 'react-redux'

import {compose, pure as renderOptimizeEffect} from 'recompose'

import {Action} from '../../action'
import {State} from '../../reducer/state'
import {
  DictionaryRegisterHandler,
  DictionaryRegisterView,
  DictionaryRegisterViewProps
} from "../../component/dictionary/RegisterView";

// ______________________________________________________
//
// @ I/F
type MappedState = {
  loading: boolean,
  errorMsg?: string
}

// ______________________________________________________
//
// @ Connect
const mapState = (state: State.Root): MappedState => {
  const {loading, error} = state.remote.dictionary.add;
  const errorMsg = error && error.message
  return {loading, errorMsg};
}


const mapDispatch = (dispatch: Dispatch): DictionaryRegisterHandler => bindActionCreators<DictionaryRegisterHandler, DictionaryRegisterHandler>({
  handleRegister: (addForm: Action.Dictionary.AddForm) => new Action.Dictionary.AddRequest(addForm).create()
}, dispatch)

const connectEffect = connect(
  mapState,
  mapDispatch,
)

const effect = compose<DictionaryRegisterViewProps, {}>(
  connectEffect,
  renderOptimizeEffect,
)

export const DictionaryAddContainer = effect(DictionaryRegisterView)
