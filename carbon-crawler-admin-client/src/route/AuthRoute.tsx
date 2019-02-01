import * as React from 'react'
import {Route, RouteProps, withRouter} from "react-router"
import {compose, pure} from 'recompose'

import {History} from "history"
import {AuthUtil} from "../service/aws";
import {path} from "./path";

type PickRequired<T, K extends keyof T> =
  Required<Pick<T, K>>
  & Omit<T, K>

type ViewProps =
  PickRequired<RouteProps, 'component'>
  & { history: History }

const RouteView = ({component: Component, history, ...rest}: ViewProps) => (<Route
  {...rest}
  render={(props) => {
    // handle Optimistically
    // noinspection JSIgnoredPromiseFromCall
    AuthUtil.shouldBeSignIn(() => history.push(path.auth.signIn))
    return <Component {...props} />
  }}
/>)

export const AuthRoute = compose<ViewProps, RouteProps>(
  withRouter,
  pure,
)(RouteView)
