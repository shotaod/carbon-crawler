import {initialState, Model, State} from './state'
import {Action} from '../action'
import {Reducer} from 'redux'

type DictionaryState = State.Dictionary
type DictionaryAction = Action.Dictionary.Actions
type DictionaryReducer = Reducer<DictionaryState, DictionaryAction>
export const dictionaryReducer: DictionaryReducer = (state: DictionaryState = initialState.dictionary, action: DictionaryAction): DictionaryState => {
  const Types = Action.Dictionary.Types

  switch (action.type) {
    case Types.DICTIONARY_FETCH_SUCCESS: {
      const {items, page} = action.payload;
      const data = items
        .map(d => {
          if (!d.memo) {
            d.memo = ''
          }
          return d as Model.Dictionary
        })
        .reduce((acc, d) => {
          acc[d.id] = d
          return acc
        }, {} as {[key in number]:Model.Dictionary});
      return {
        data,
        page,
      }
    }
    default:
      return state
  }
}
