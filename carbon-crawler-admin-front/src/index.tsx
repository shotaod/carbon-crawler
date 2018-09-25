import * as React from 'react'
import * as ReactDOM from 'react-dom'
import {Provider} from 'react-redux'
import {createBrowserHistory} from 'history'

import {rootSaga} from "./saga"
import {configureSagaStore} from './store'
import {Application} from "./app"

const sagaStore = configureSagaStore()
const history = createBrowserHistory()

sagaStore.run(rootSaga)

ReactDOM.render(
  <Provider store={sagaStore}>
    <Application history={history}/>
  </Provider>,
  document.getElementById('root')
)
