import * as React from 'react'
import styled from 'styled-components'
import {SideBar, width} from './sidebar'
import {ToastContainer} from "react-toastify";

export type ViewProp = {
  currentPath: string
  signIn: boolean
  children: JSX.Element | JSX.Element[]
}

const SectionView = styled.section <{ withSideBar: boolean }>`
  position: relative;
  ${p => p.withSideBar ? `margin-left: ${width}` : ''}
`

export const Body = (props: ViewProp) => (
  <>
    {props.signIn && <SideBar {...props}/>}
    <SectionView withSideBar={props.signIn}>
      <ToastContainer
        closeButton={false}
        hideProgressBar
      />
      {props.children}
    </SectionView>
  </>
)
