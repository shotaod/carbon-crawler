import * as React from 'react'
import {History} from 'history'
import {Route, Switch} from 'react-router'
import {ConnectedRouter} from 'react-router-redux'

import {AuthRoute} from './AuthRoute'
import {routes} from "./routes";

import {
  DictionaryAddContainer,
  DictionaryListContainer,
  LayoutContainer,
  SignInContainer,
  SignUpContainer,
  TopContainer,
} from '../container'

export const RoutesContainer = (props: { history: History }) => (
  <ConnectedRouter history={props.history}>
    <Switch>
      <LayoutContainer>
        <Route exact path='/' component={TopContainer}/>
        <Route exact path={routes.auth.signIn} component={SignInContainer}/>
        <Route exact path={routes.auth.signUp} component={SignUpContainer}/>
        <AuthRoute exact path={routes.dictionary.list} component={DictionaryListContainer}/>
        <AuthRoute exact path={routes.dictionary.register} component={DictionaryAddContainer}/>
      </LayoutContainer>
    </Switch>
  </ConnectedRouter>
)
