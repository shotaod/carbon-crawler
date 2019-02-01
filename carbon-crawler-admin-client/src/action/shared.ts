import {AnyAction} from 'redux'

export type FsaAction<Payload> = { type: string, payload: Payload, error: boolean, meta?: any }

export abstract class FsaActionCreator<Type extends string, Payload = {}> implements AnyAction {
  type: Type
  payload: Payload
  error?: boolean
  meta?: any

  protected constructor(type: Type, payload: Payload, error: boolean = false, meta?: any) {
    this.type = type
    this.payload = payload
    this.error = error
    this.meta = meta
  }

  // https://github.com/reduxjs/redux/issues/2361
  create(): FsaAction<Payload> {
    const {type, payload, error, meta} = this
    return {
      type,
      payload,
      error: !!error,
      meta,
    }
  }
}
