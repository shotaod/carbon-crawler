import {compose, pure as renderOptimizeEffect} from 'recompose'
import {connect} from 'react-redux'

import {State} from '../../reducer/state'
import {LayoutView} from '../../component/layout'

type Props = { children: JSX.Element | JSX.Element[] }

type MappedState = {
  currentPath: string
  login: boolean
}

type ViewProp = Props & MappedState

const mapState = (state: State.Root): MappedState => ({
  currentPath: state.router.location ? state.router.location.pathname : '',
  login: state.auth.login,
})

const connectEffect = connect(
  mapState
)

const effect = compose<ViewProp, Props>(
  connectEffect,
  renderOptimizeEffect,
)

export const LayoutContainer = effect(LayoutView)
