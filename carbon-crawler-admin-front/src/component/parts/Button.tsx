import styled, {css} from "styled-components"
import {Link} from "react-router-dom"

const buttonBase = css`
  border: none
  padding: 5px 20px
  font-size: 15px
  border-radius: 2px
  cursor: pointer
  text-decoration: none
  
  :focus {
    outline: none
    box-shadow: 0px 1px 1px 1px #aaa
  }
  
  :hover {
    box-shadow: 0px 1px 1px 1px #aaa
  }
`

const primaryStyle = css`
  background-color: #2e7c9c
  color: #fff
`

export const PrimaryButton = styled.button`
  ${buttonBase}
  ${primaryStyle}
`

export const PrimaryLinkButton = styled(Link)`
  ${buttonBase}
  ${primaryStyle}
`
