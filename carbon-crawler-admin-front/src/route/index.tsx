import * as React from 'react'
import {History} from 'history'
import {Route, Switch} from 'react-router'
import {ConnectedRouter} from 'react-router-redux'

import {AuthRoute} from './AuthRoute'
import {path} from "./path";

import {TroublePage,} from "../component/auth";
import {DictionaryAddContainer, DictionaryListContainer, LayoutContainer, TopContainer,} from '../container'

import {ChangePasswordContainer, ForgotPasswordContainer, SignInContainer, SignUpContainer,} from "../container/auth";

export const RoutesContainer = (props: { history: History }) => (
  <ConnectedRouter history={props.history}>
    <Switch>
      <LayoutContainer>
        <Route exact path='/' component={TopContainer}/>
        <Route exact path={path.auth.signIn} component={SignInContainer}/>
        <Route exact path={path.auth.signUp} component={SignUpContainer}/>
        <Route exact path={path.auth.trouble.index} component={TroublePage}/>
        <Route exact path={path.auth.trouble.forgotPassword} component={ForgotPasswordContainer}/>
        <Route exact path={path.auth.trouble.resendConfirmMail} component={TroublePage}/>
        <AuthRoute exact path={path.auth.trouble.changePassword} component={ChangePasswordContainer}/>
        <AuthRoute exact path={path.dictionary.list} component={DictionaryListContainer}/>
        <AuthRoute exact path={path.dictionary.register} component={DictionaryAddContainer}/>
      </LayoutContainer>
    </Switch>
  </ConnectedRouter>
)
