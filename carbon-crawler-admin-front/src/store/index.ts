import {Action, AnyAction, applyMiddleware, compose, createStore, Store} from 'redux'
import createSagaMiddleware, {END} from 'redux-saga'

import {rootReducer} from '../reducer'

export interface Saga {
  run: (any: any) => any
  close: () => any
}

type reduxWindow = {
  __REDUX_DEVTOOLS_EXTENSION_COMPOSE__?: typeof compose
}
const enhancedCompose = (window as reduxWindow).__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose

export type SagaStore<S = any, A extends Action = AnyAction> = Store<S, A> & Saga

export const configureSagaStore = (initialState: any = {}): SagaStore => {
  const sagaMiddleware = createSagaMiddleware()

  const store = createStore(
    rootReducer,
    initialState,
    enhancedCompose(
      applyMiddleware(
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
