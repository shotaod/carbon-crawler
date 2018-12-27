import {Action, AnyAction, applyMiddleware, compose, createStore, Store} from 'redux'
import createSagaMiddleware, {END} from 'redux-saga'

import {rootReducer} from '../reducer'
import {routerMiddleware as createRouterMiddleware} from "react-router-redux";
import {History} from "history";

type Saga = {
  run: (any: any) => any
  close: () => any
}

type reduxWindow = {
  __REDUX_DEVTOOLS_EXTENSION_COMPOSE__?: typeof compose
}
const enhancedCompose = (window as reduxWindow).__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose

type SagaStore<S = any, A extends Action = AnyAction> = Store<S, A> & Saga

export const configureSagaStore = (history: History, initialState: any = {}): SagaStore => {
  const sagaMiddleware = createSagaMiddleware()
  const routerMiddleware = createRouterMiddleware(history);

  const store = createStore(
    rootReducer,
    initialState,
    enhancedCompose(
      applyMiddleware(
        routerMiddleware,
        sagaMiddleware,
      ),
    )
  ) as SagaStore

  if (module.hot) {
    // Enable Webpack hot module replacement for reducers
    module.hot.accept('../reducer', () => {
      const nextRootReducer = require('../reducer').default
      store.replaceReducer(nextRootReducer)
    })
  }

  store.run = sagaMiddleware.run
  store.close = () => store.dispatch(END)
  return store
}
