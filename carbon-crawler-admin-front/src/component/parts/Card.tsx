import * as React from "react";
import styled from "styled-components";

export type CardProp = {
  className?: string,
  title: string,
  actionElement?: React.ReactNode,
  children: React.ReactNode,
  margin?: number,
}

const _Card = ({className, title, actionElement, children}: CardProp) =>
  <section className={className}>
    <h3>{title}</h3>
    <span>
    {actionElement}
    </span>
    <hr/>
    {children}
  </section>

export const Card = styled(_Card)`
  background-color: #fff;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 1px 5px 1px #afbac880;
  > h3 {
    margin: 0;
    display: inline-block;
    color: #252422;
    font-weight: 300;
  }
  > span {
    float: right;
    opacity: 0;
    transition: opacity 0.3s;
  }

  :hover > span {
    opacity: 1;
  }
`
