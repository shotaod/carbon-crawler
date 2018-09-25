import createRouterProxy from 'react-cosmos-router-proxy';
import createFormikProxy from './src/__cosmos__/react-cosmos-formik-proxy'
import createStyleProxy from './src/__cosmos__/react-cosmos-style-proxy'

// noinspection JSUnusedGlobalSymbols
export default [
  createFormikProxy(),
  createRouterProxy(),
  createStyleProxy({
    padding: '10px',
  }),
]
