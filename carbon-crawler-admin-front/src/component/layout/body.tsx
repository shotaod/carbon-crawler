import * as React from 'react'
import styled from 'styled-components'
import {SideBar, width} from './sidebar'
import {render} from '../../utils';

export type ViewProp = {
  currentPath: string
  signIn: boolean
  children: JSX.Element | JSX.Element[]
}

const SectionView = styled.section <{ withSideBar: boolean }>`
  ${p => p.withSideBar ? `margin-left: ${width}` : ''} 
`

export const Body = (props: ViewProp) => (
  <>
    {render(props.signIn ? (<SideBar currentPath={props.currentPath}/>) : null)}
    <SectionView withSideBar={props.signIn}>
      {props.children}
    </SectionView>
  </>
)
