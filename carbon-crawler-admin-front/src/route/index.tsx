import * as React from 'react'
import {History} from 'history'
import {Route, Switch} from 'react-router'
import {ConnectedRouter} from 'react-router-redux'

import {AuthRoute} from './AuthRoute'
import {routes} from "./routes";

import {DictionaryAddContainer, DictionaryListContainer, LayoutContainer, TopContainer,} from '../container'
import {TroubleView} from "../component/auth";
import {ChangePasswordContainer, ForgotPasswordContainer, SignInContainer, SignUpContainer,} from "../container/auth";

export const RoutesContainer = (props: { history: History }) => (
  <ConnectedRouter history={props.history}>
    <Switch>
      <LayoutContainer>
        <Route exact path='/' component={TopContainer}/>
        <Route exact path={routes.auth.signIn} component={SignInContainer}/>
        <Route exact path={routes.auth.signUp} component={SignUpContainer}/>
        <Route exact path={routes.auth.trouble.index} component={TroubleView}/>
        <Route exact path={routes.auth.trouble.forgetPassword} component={ForgotPasswordContainer}/>
        <Route exact path={routes.auth.trouble.resendConfirmMail} component={TroubleView}/>
        <AuthRoute exact path={routes.auth.trouble.changePassword} component={ChangePasswordContainer}/>
        <AuthRoute exact path={routes.dictionary.list} component={DictionaryListContainer}/>
        <AuthRoute exact path={routes.dictionary.register} component={DictionaryAddContainer}/>
      </LayoutContainer>
    </Switch>
  </ConnectedRouter>
)
