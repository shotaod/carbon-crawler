import {compose, pure as renderOptimizeEffect} from 'recompose'

import {connect} from 'react-redux'
import {State} from '../../reducer/state'
import {LayoutView} from '../../component/layout'

type MappedState = {
  currentPath: string
}

const mapState = (state: State.Root): MappedState => ({
  currentPath: state.router.location ? state.router.location.pathname : '',
})

const connectEffect = connect(
  mapState
)

const effect = compose<MappedState, {}>(
  connectEffect,
  renderOptimizeEffect,
)

export const LayoutContainer = effect(LayoutView)
