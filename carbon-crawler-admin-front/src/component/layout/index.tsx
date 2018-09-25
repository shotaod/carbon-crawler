import * as React from "react"
import {compose, pure as renderOptimizeEffect} from "recompose"
import {connect} from "react-redux"

import {State} from "../../reducer/state"
import {Header} from "./header"
import {SideBar} from "./sidebar"
import {MainSection} from "./main"

type Props = { children: JSX.Element }

type MappedState = {
  currentPath: string
}

type ViewProp = Props & MappedState

export const View = (props: ViewProp) => (
  <div>
    <Header/>
    <SideBar currentPath={props.currentPath}/>
    <MainSection>
      {props.children}
    </MainSection>
  </div>
)

const mapState = (state: State.Root): MappedState => ({
  currentPath: state.router.location ? state.router.location.pathname : ''
})

const connectEffect = connect(
  mapState
)

const effect = compose<ViewProp, Props>(
  connectEffect,
  renderOptimizeEffect,
)

export const LayoutContainer = effect(View)
