import * as React from 'react'
import * as ReactDOM from 'react-dom'
import {Provider} from 'react-redux'
import {createBrowserHistory} from 'history'

import {rootSaga} from './saga'
import {aws} from './service/aws'
import {configureSagaStore} from './store'
import {Application} from './app'

const history = createBrowserHistory()
const sagaStore = configureSagaStore(history)

aws.configure()
sagaStore.run(rootSaga)

ReactDOM.render(
  <Provider store={sagaStore}>
    <Application history={history}/>
  </Provider>,
  document.getElementById('root')
)
