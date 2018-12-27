import * as React from "react";
import styled from "styled-components";
import {Row} from "./Row";

const Panel = styled.p`
  padding: 10px;
  background-color: #ff5e5e;
  margin: 10px;
  color: #fff;
  /* opacity: 0.65; */
  border-radius: 2px;
  width: 100%;
`
export const ErrorPanel = ({text}: { text: string }) => (<Row><Panel>{text}</Panel></Row>)
export const ErrorLabel = styled.p`
  display: inline-block
  font-size: 10px
  border-radius: 2px
  margin: 1px 0 0 0 
  padding: 5px 10px
  color: #fff
  background-color: #ff5e5e
`
