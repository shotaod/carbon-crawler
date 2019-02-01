import * as React from "react";
import styled from "styled-components";

type PanelProp = {
  className?: string
  text: string
}
const _BasePanel = ({className, text}: PanelProp) => (<div className={className}><p>{text}</p></div>)
const BasePanel = styled<React.StatelessComponent<PanelProp & { bColor: string }>>(_BasePanel)`
  box-sizing: border-box;
  padding: 20px;
  width: 100%;
  opacity: 0.7;
  
  p {
    color: #fff;
    background-color: ${({bColor}) => bColor};
    padding: 10px;
    border-radius: 2px;
    font-weight: 500;
  }
`
export const InfoPanel = (prop: PanelProp) => (<BasePanel bColor='#2e7c9c' {...prop}/>)
export const ErrorPanel = (prop: PanelProp) => (<BasePanel bColor='#ff5e5e' {...prop}/>)
export const ErrorLabel = styled.p`
  display: inline-block
  font-size: 10px
  border-radius: 2px
  margin: 1px 0 0 0 
  padding: 5px 10px
  color: #fff
  background-color: #ff5e5e
`
