import styled, {injectGlobal} from 'styled-components';
import * as React from 'react';
import {Body, Footer, Header, ViewProp as BodyProp} from './'

export * from './header'
export * from './body'
export * from './sidebar'
export * from './footer'

type ViewProp = BodyProp

injectGlobal`
  a, button {
    font-family: sans-serif;
  }  
`
const FlexColumn = styled.div`
  display: flex
  flex-direction: column
  height: 100vh
`

export const LayoutView = (props: ViewProp) => (
  <FlexColumn>
    <Header/>
    <Body {...props}>
    {props.children}
    </Body>
    <Footer/>
  </FlexColumn>
)
