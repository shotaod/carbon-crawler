import * as React from "react";
import {Header, MainSection, SideBar} from './'

export * from './header'
export * from './main'
export * from './sidebar'

type ViewProp = {
  currentPath: string
  children: JSX.Element | JSX.Element[]
}

export const LayoutView = (props: ViewProp) => (
  <div>
    <Header/>
    <SideBar currentPath={props.currentPath}/>
    <MainSection>
      {props.children}
    </MainSection>
  </div>
)
