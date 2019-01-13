import {initialState, State} from './state'
import {Action} from '../action'
import {Reducer} from 'redux'

type SnapState = Dig<State.Root, 'snap'>
type SnapAction = Action.Snap.Actions
type SnapReducer = Reducer<SnapState, SnapAction>
const Types = Action.Snap.Types

export const snapReducer: SnapReducer = (state: SnapState = initialState.snap, action: SnapAction): SnapState => {
  switch (action.type) {
    case Types.SNAP_FETCH_SUCCESS: {
      const {items, page} = action.payload
      const newItems = items
        .map(item => {
          const pages = item.pages
            .map(page => ({[page.id]: page}))
            .reduce((acc, p) => ({...acc, ...p}))
          return ({[item.id]: {...item, pages}})
        })
        .reduce((acc, item) => ({...acc, ...item}))
      return {
        items: {...state.items, ...newItems},
        page,
      }
    }
    default:
      return state
  }
}
