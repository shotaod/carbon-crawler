import {compose} from 'recompose'
import {History} from 'history'
import {hot} from 'react-hot-loader'
import {RoutesContainer} from '../route'
import {env} from "../config";

console.log(env.isProd, env.isProd)
// if dev
export const Application = env.isDev
  ? compose<{ history: History }, { history: History }>(hot(module))(RoutesContainer)
  : RoutesContainer
