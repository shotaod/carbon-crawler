import {combineReducers} from 'redux'
import {routerReducer} from 'react-router-redux'
import {State} from './state'

import {authReducer} from './auth'
import {queryReducer} from './query'
import {remoteReducer} from './remote'
import {snapReducer} from "./snap";

// NOTE: current type definition of Reducer in 'react-router-redux' and 'redux-actions' module
// doesn't go well with redux@4
export const rootReducer = combineReducers<State.Root>({
  router: routerReducer,
  remote: remoteReducer,
  auth: authReducer,
  query: queryReducer,
  snap: snapReducer,
})
