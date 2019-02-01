import * as React from "react";
import {lifecycle} from "recompose";
import {toast} from "react-toastify";
import {ErrorPanel, InfoPanel} from "../parts";

export type Message = {
  errorMsg?: string,
  successMsg?: string,
}

export function messageHandleEffect<Props>(
  {
    onSuccess,
    onError,
  }: {
    onSuccess?: (p: Props) => void,
    onError?: (p: Props) => void,
  } = {}) {
  return lifecycle<Message & Props, {}>({
    componentWillReceiveProps({successMsg, errorMsg}) {
      if (!this.props.successMsg && successMsg) {
        onSuccess && onSuccess(this.props)
        toast(() => <InfoPanel text={successMsg}/>, {
          autoClose: 3000,
          pauseOnHover: true,
          pauseOnFocusLoss: true,
        })
      }
      if (!this.props.errorMsg && errorMsg) {
        onError && onError(this.props)
        toast(() => <ErrorPanel text={errorMsg}/>, {
          autoClose: 3000,
          pauseOnHover: true,
          pauseOnFocusLoss: true,
        })
      }
    }
  })
}
