import * as React from "react"
import {History} from "history"
import {Route, Switch} from "react-router"
import {ConnectedRouter} from "react-router-redux"

import {
  AuthenticationContainer, DictionaryAddContainer, DictionaryListContainer, LayoutContainer,
  TopContainer,
} from "../container"
import {Pages} from "./page"

export const Routes = (props: { history: History }) => (
  <ConnectedRouter history={props.history}>

    <Switch>
      <Route exact path={Pages.AUTH} component={AuthenticationContainer}/>
      <LayoutContainer>
        <Route exact path="/" component={TopContainer}/>
        <Route exact path={Pages.DICTIONARY_LIST} component={DictionaryListContainer}/>
        <Route exact path={Pages.DICTIONARY_REGISTER} component={DictionaryAddContainer}/>
      </LayoutContainer>
    </Switch>

  </ConnectedRouter>
)
