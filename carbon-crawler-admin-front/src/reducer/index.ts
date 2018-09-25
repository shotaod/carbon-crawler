import {combineReducers} from 'redux'
import {dictionaryReducer} from './dictionary'
import {routerReducer} from 'react-router-redux'
import {State} from './state'
import {remoteReducer} from "./remote"

// NOTE: current type definition of Reducer in 'react-router-redux' and 'redux-actions' module
// doesn't go well with redux@4
export const rootReducer = combineReducers<State.Root>({
  remote: remoteReducer,
  dictionary: dictionaryReducer,
  router: routerReducer,
})
