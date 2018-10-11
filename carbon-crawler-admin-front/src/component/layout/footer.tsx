import * as React from 'react'
import styled from 'styled-components'

export const Footer = () => (
  <FooterRow>
    <Copy title='Carbon'/>
  </FooterRow>
)

const FooterRow = styled.footer`
  margin-top: auto
  display: flex
  justify-content: center
  z-index: 100
  
  background-color: gray
  color: #fff
  font-size: 20px
`

const Copy = (prop: { title: string }) => (
  <p>
    &copy; {prop.title}
  </p>
)
