import * as _ from 'lodash'
import {Reducer} from 'redux'
import {LOCATION_CHANGE} from "react-router-redux";
import {initialState, State} from './state'
import {Action} from '../action'
import {REMOTE_RESET} from "../action/remote";

type RemoteState = State.Remote<State.Common.CallState>
type RemoteAction = Action.Remote.Actions
type RemoteReducer = Reducer<RemoteState, RemoteAction>

type HandlerHook = { begins: string[], ends: string[] }
type Handler = { [k in string]: HandlerHook }
type RemoteConfig = State.Remote<HandlerHook>
const configureRemoteReducer = (config: RemoteConfig): (state: RemoteState | undefined, action: RemoteAction) => RemoteState => {
  enum Status {
    start,
    end,
  }

  type ComputeHandler = {
    domainKey: string
    apiKey: string
    types: string[]
    status: Status
  }

  const mapToCompute = (domainKey: string, apiKey: string, types: string[], status: Status): ComputeHandler => ({
    domainKey,
    apiKey,
    types,
    status,
  })

  const computeHandlers = _.chain(config)
    .entries()
    .map(e => ({
      key: e[0],
      handler: e[1] as Handler,
    }))
    .flatMap(kSub => {
      const {key, handler} = kSub
      return _.entries(handler)
        .map(he => ({
          domainKey: key,
          apiKey: he[0],
          handler: he[1] as HandlerHook,
        }))
        .reduce((handlers, h) => {
          const {domainKey, apiKey, handler: {begins, ends}} = h
          const additions = [
            mapToCompute(domainKey, apiKey, begins, Status.start),
            mapToCompute(domainKey, apiKey, ends, Status.end)
          ]
          return _.concat(handlers, additions)
        }, [] as ComputeHandler[])
    })
    .value()

  return (state = initialState.remote, action) => {

    for (const handler of computeHandlers) {
      const {type, error} = action;

      if ((action as any).type === LOCATION_CHANGE) {
        const errorKeys = _.chain<string[]>(_.keys(state))
          .flatMap(domain => _.keys(_.get(state, domain))
            .map(operation => `${domain}.${operation}.error`)
          ).value()
        return _.omit(state, errorKeys)
      }

      if ((action as any).type === REMOTE_RESET) {
        return initialState.remote
      }

      if (handler.types.includes(type)) {
        const {domainKey, apiKey, status} = handler
        const errorEntry = {message: (error && (action.payload as any).message) || undefined}
        const newValue = _.set({}, [apiKey], {
          loading: status === Status.start,
          error: errorEntry,
          success: !error && status === Status.end,
        })
        const callState = Object.assign({}, state[domainKey as keyof RemoteState], newValue)
        const newDomainCallState = _.set({}, domainKey, callState)
        return Object.assign({}, state, newDomainCallState)
      }
    }

    return state
  }
}

export const remoteReducer: RemoteReducer = configureRemoteReducer({
  query: {
    fetch: {
      begins: [Action.Query.Types.QUERY_FETCH_REQUEST],
      ends: [Action.Query.Types.QUERY_FETCH_SUCCESS, Action.Query.Types.QUERY_FETCH_FAILURE],
    },
    add: {
      begins: [Action.Query.Types.QUERY_ADD_REQUEST],
      ends: [Action.Query.Types.QUERY_ADD_SUCCESS, Action.Query.Types.QUERY_ADD_FAILURE],
    },
    put: {
      begins: [Action.Query.Types.QUERY_PUT_REQUEST],
      ends: [Action.Query.Types.QUERY_PUT_SUCCESS, Action.Query.Types.QUERY_PUT_FAILURE],
    },
  },
  snap: {
    fetch: {
      begins: [Action.Snap.Types.SNAP_FETCH_REQUEST],
      ends: [Action.Snap.Types.SNAP_FETCH_SUCCESS, Action.Snap.Types.SNAP_FETCH_FAILURE]
    }
  }
})
