import * as React from 'react'
import styled from 'styled-components'
import {ToastContainer} from "react-toastify";

export type ViewProp = {
  currentPath: string
}

const SectionView = styled.section`
  position: relative;
`

export const Body: React.StatelessComponent<ViewProp> = props => (
  <SectionView>
    <ToastContainer closeButton={false} hideProgressBar/>
    {props.children}
  </SectionView>
)
