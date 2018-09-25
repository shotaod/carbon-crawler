import * as React from "react"
import {History} from "history"
import {Route, Switch} from "react-router"
import {ConnectedRouter} from "react-router-redux"

import {LayoutContainer} from "../component/layout"
import {Top} from '../component/home'
import {DictionaryAddContainer} from '../container/dictionary/register'
import {DictionaryListContainer} from "../component/dictionary"
import {Pages} from "./page"

export const Routes = (props: { history: History }) => (
  <ConnectedRouter history={props.history}>
    <LayoutContainer>
      <Switch>
        <Route exact path="/" component={Top}/>
        <Route exact path={Pages.DICTIONARY_LIST} component={DictionaryListContainer}/>
        <Route exact path={Pages.DICTIONARY_REGISTER} component={DictionaryAddContainer}/>
      </Switch>
    </LayoutContainer>
  </ConnectedRouter>
)
