import {compose} from 'recompose'
import {History} from "history"
import {hot} from 'react-hot-loader'
import {Routes} from "../route"

// if dev
export const Application = compose<{ history: History }, { history: History }>(
  hot(module),
)(Routes)
