import * as React from 'react'
import styled from 'styled-components'
import {SideBar, width} from './sidebar'

export type ViewProp = {
  currentPath: string
  signIn: boolean
  children: JSX.Element | JSX.Element[]
}

const SectionView = styled.section <{ withSideBar: boolean }>`
  ${p => p.withSideBar ? `margin-left: ${width}` : ''} 
`

const SideBarAdjust = (props: { currentPath: string }) => <div><SideBar {...props} /></div>

export const Body = (props: ViewProp) => (
  <>
    {props.signIn && <SideBarAdjust {...props}/>}
    <SectionView withSideBar={props.signIn}>
      {props.children}
    </SectionView>
  </>
)
