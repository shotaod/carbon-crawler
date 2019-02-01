import styled, {css} from 'styled-components'
import {Link} from 'react-router-dom'
import * as React from "react";
import {MouseEventHandler} from "react";
import {FontAwesomeIcon, Props} from "@fortawesome/react-fontawesome";

const buttonBase = css`
  border: none
  padding: 5px 20px
  font-size: 15px
  line-height: 1.5em;
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

const secondaryStyle = css`
  background-color: #aaa
  color: #fff
`

type ButtonProp = Partial<Props> & {
  type?: string,
  onClick?: MouseEventHandler<HTMLButtonElement>,
  iconColor?: string,
  children: React.ReactNode,
}
const _Button = (
  {
    className,
    type,
    onClick,
    children,
  }: ButtonProp) => (
  <button type={type} className={className} onClick={onClick}>
    {children}
  </button>
)
export const PrimaryButton = styled<React.StatelessComponent<ButtonProp>>(_Button)`
  ${buttonBase}
  ${primaryStyle}
  svg {
    padding-right: 10px;
  }
`

export const PlainButton = styled<React.StatelessComponent<ButtonProp>>(_Button)`
  ${buttonBase}
`

// ______________________________________________________
//
// @ Link Button
export const PrimaryLinkButton = styled(Link)`
  ${buttonBase}
  ${primaryStyle}
  :visited {
    color: #fff;
  }
`

export const SecondaryLinkButton = styled(Link)`
  ${buttonBase}
  ${secondaryStyle}
  :visited {
    color: #fff;
  }
`

export const TextLinkButton = styled(Link)`
    text-decoration: underline;
    color: #000;
`

// ______________________________________________________
//
// @ Icon Button
const iconColorStyle = css<{ iconColor?: string }>`
  ${({iconColor}) => iconColor ? `
  svg > path {
    fill: ${iconColor}
  }
  ` : ''}
`

type IconButtonProp = Props
  & {
  className?: string,
  iconColor?: string,
  onClick: MouseEventHandler<HTMLButtonElement>
}
type IconLinkProp = Props
  & {
  className?: string,
  iconColor?: string,
  to: string,
}
const _IconButton = ({
                       className,
                       onClick,
                       iconColor,
                       ...rest
                     }: IconButtonProp) => (
  <button className={className} onClick={onClick}>
    <FontAwesomeIcon {...rest}/>
  </button>
)

export const IconButton = styled<React.StatelessComponent<IconButtonProp>>(_IconButton)`
  border: none;
  padding: 0;
  background-color: transparent;
  cursor: pointer;
  :focus {
    outline: none;
  }

  ${iconColorStyle}
`

const _IconLink = ({
                     className,
                     to,
                     iconColor,
                     ...rest
                   }: IconLinkProp) => (
  <Link className={className} to={to}>
    <FontAwesomeIcon {...rest}/>
  </Link>
)

export const IconLink = styled<React.StatelessComponent<IconLinkProp>>(_IconLink)`
  border: none;
  padding: 0;
  background-color: transparent;
  cursor: pointer;
  :focus {
    outline: none;
  }

  ${iconColorStyle}
`
