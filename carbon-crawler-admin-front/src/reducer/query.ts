import {initialState, State} from './state'
import {Action} from '../action'
import {Reducer} from 'redux'

type QueryState = Dig<State.Root, 'query'>
type QueryAction = Action.Query.Actions
type QueryReducer = Reducer<QueryState, QueryAction>
export const queryReducer: QueryReducer = (state: QueryState = initialState.query, action: QueryAction): QueryState => {
  const Types = Action.Query.Types

  switch (action.type) {
    case Types.QUERY_FETCH_SUCCESS: {
      const {items, page} = action.payload;
      const newItems = items
        .map(item => ({[item.id]: item}))
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
