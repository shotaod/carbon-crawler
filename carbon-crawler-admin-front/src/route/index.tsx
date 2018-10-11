import * as React from 'react'
import {History} from 'history'
import {Route, Switch} from 'react-router'
import {ConnectedRouter} from 'react-router-redux'

import {
  DictionaryAddContainer, DictionaryListContainer, LayoutContainer, SignUpContainer,
  TopContainer,
} from '../container'
import {SignInContainer} from "../container/auth/signIn";
import {routes} from "./routes";

export const RoutesContainer = (props: { history: History }) => (
  <ConnectedRouter history={props.history}>
    <Switch>
      <LayoutContainer>
        <Route exact path='/' component={TopContainer}/>
        <Route exact path={routes.auth.login} component={SignInContainer}/>
        <Route exact path={routes.auth.signup} component={SignUpContainer}/>
        <Route exact path={routes.dictionary.list} component={DictionaryListContainer}/>
        <Route exact path={routes.dictionary.register} component={DictionaryAddContainer}/>
      </LayoutContainer>
    </Switch>
  </ConnectedRouter>
)
