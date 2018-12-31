import * as React from 'react'
import {connect} from "react-redux"
import {Redirect, Route, RouteProps} from "react-router"
import * as PropTypes from 'prop-types'
import {compose, pure, withContext} from 'recompose'

import {Location} from "history"

import {State} from "../reducer/state"
import {path} from "./path"

type PickRequired<T, K extends keyof T> =
  Required<Pick<T, K>>
  & Omit<T, K>

type AuthProps = {
  signIn: boolean,
}
type LocationProps = {
  location: Location | null
}
type MappedProps = AuthProps & LocationProps

type ViewProps =
  PickRequired<RouteProps, 'component'>
  & MappedProps

const RouteView = ({component: Component, ...rest}: ViewProps) => (<Route
  {...rest}
  render={(props) => (rest.signIn
    ? <Component {...props} />
    : <Redirect to={path.auth.signIn}/>)}
/>)

export const AuthRoute = compose<ViewProps, RouteProps>(
  connect(
    (state: State.Root): MappedProps => ({
      signIn: state.auth.signIn,
      location: state.router.location,
    })
  ),
  withContext<LocationProps, LocationProps>(
    {location: PropTypes.any},
    ({location}) => ({location})
  ),
  pure,
)(RouteView)
