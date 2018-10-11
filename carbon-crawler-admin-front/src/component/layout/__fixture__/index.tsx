import * as React from 'react';
import {Header} from '../header'
import {SideBar} from '../sidebar'
import {withName} from '../../../__cosmos__/helper';

// noinspection JSUnusedGlobalSymbols
export default [
  {
    component: withName('layout/Header')(Header),
    style: {
      padding: 0
    },
  },
  {
    component: withName('layout/SideBar')((props: any) => <SideBar
      currentPath={props.location ? props.location.pathname : ''}/>),
    provideRouterProps: true,
    url: '/',
    route: '/',
    style: {
      padding: 0
    },
  },
]
