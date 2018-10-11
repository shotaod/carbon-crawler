import * as React from 'react';
import {PrimaryButton, StrokeLoader, Title} from '../'
import {withName} from '../../../__cosmos__/helper';

// noinspection JSUnusedGlobalSymbols
export default [
  {
    component: withName('parts/PrimaryButton')(() => <PrimaryButton>button</PrimaryButton>),
  },
  {
    component: withName('parts/Loader')(StrokeLoader),
  },
  {
    component: withName('parts/Title')(() => <Title>title</Title>)
  }
]
