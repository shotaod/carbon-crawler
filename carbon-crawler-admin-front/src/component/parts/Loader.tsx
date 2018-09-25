import * as React from 'react'
import styled, {keyframes} from "styled-components"

const loader = (props: { className?: string }) => (
  <div className={props.className}>
    <div/>
    <div/>
    <div/>
    <div/>
    <div/>
  </div>
)

const stroke = keyframes`
  0%, 40%, 100% { 
    transform: scaleY(0.4)
  }  
  20% { 
    transform: scaleY(1.0)
  }
`

export const StrokeLoader = styled(loader)`
  width: 50px
  height: 60px
  text-align: center
  font-size: 10px

  div {
    background-color: #aaa
    height: 100%
    width: 6px
    display: inline-block
    margin-right: 3px
    
    animation: 1.2s ${stroke} infinite ease-in-out
  } 
  > div:nth-child(2) {
    animation-delay: -1.1s
  }
  
  > div:nth-child(3) {
    animation-delay: -1.0s
  }
  
  > div:nth-child(4) {
    animation-delay: -0.9s
  }
  
  > div:nth-child(5) {
    animation-delay: -0.8s
  }
`
