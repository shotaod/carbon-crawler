import {createGlobalStyle} from 'styled-components';
import * as React from 'react';
import {Body, ViewProp} from './'
import 'react-toastify/dist/ReactToastify.min.css';

export * from './body'
export * from './sidebar'

const GlobalStyle = createGlobalStyle`
  * {
    -webkit-font-smoothing: antialiased;
    font-family: "Helvetica", Arial, sans-serif;
    font-weight: 300;
  }
  h1,h2,h3,h4,h5,p {
    margin: 0;
  }
  a {
    text-decoration: none
  }
  a:visited {
    color: #000;
  }
  body {
    background-color: #eceff2;
  }
 
  .Toastify {
    position: sticky;
    top: 0
  }
  .Toastify__toast-container {
    position: absolute !important;
    width: 100% !important;
    padding: 0 !important;
    top: 0 !important;
    right: unset !important;
  }
  .Toastify__toast {
    box-shadow: none !important;
    margin: 0 !important;
    padding: 0 !important;
  }
  .Toastify__toast--default {
    background-color: transparent !important;
  }
`;

export const LayoutView: React.StatelessComponent<ViewProp> = ({children, ...rest}) => (<>
  <Body {...rest}>
  {children}
  </Body>
  <GlobalStyle/>
</>)
