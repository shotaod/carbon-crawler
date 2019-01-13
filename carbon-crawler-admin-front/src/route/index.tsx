import * as React from 'react'
import {History} from 'history'
import {Route, Switch} from 'react-router'
import {ConnectedRouter} from 'react-router-redux'

import {AuthRoute} from './AuthRoute'
import {path} from "./path";

import {TroublePage,} from "../component/page/auth";
import {
  LayoutContainer,
  QueryAddContainer,
  QueryDetailContainer,
  QueryTableContainer,
  SnapListViewContainer,
  SnapPageListViewContainer,
  TopContainer,
} from '../container'

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
        <AuthRoute exact path={path.query.list} component={QueryTableContainer}/>
        <AuthRoute exact path={path.query.detail} component={QueryDetailContainer}/>
        <AuthRoute exact path={path.query.register} component={QueryAddContainer}/>
        <AuthRoute exact path={path.snap.list} component={SnapListViewContainer}/>
        <AuthRoute path={path.snap.pages} component={SnapPageListViewContainer}/>
      </LayoutContainer>
    </Switch>
  </ConnectedRouter>
)
