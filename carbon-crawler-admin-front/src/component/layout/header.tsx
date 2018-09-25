import * as React from "react"
import styled from "styled-components"

export const Header = () => (
  <HeaderRow>
    <Brand>Carbon Crawler Admin</Brand>
  </HeaderRow>
)

const HeaderRow = styled.div`
  display: flex
  background-color: gray
`
const Brand = styled.h1`
  margin: 10px
  color: #fff
  font-size: 24px
`
