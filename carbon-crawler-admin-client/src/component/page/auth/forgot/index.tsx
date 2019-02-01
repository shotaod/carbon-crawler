import {compose, lifecycle, pure, withStateHandlers} from "recompose";
import * as React from "react";
import {Redirect} from "react-router";
import {CodeConfirmView, ConfirmCodeSendView, ForgotSendHandler,} from "./";
import {ChangePasswordView} from "../ChangePassword";
import {path} from "../../../../route/path";

export * from './ConfirmCodeSendView'
export * from './CodeConfirmView'

type ForgotPasswordRequirement = {
  email: string,
  code: string,
  password: string,
}

enum Step {
  ERROR = 'ERROR',
  SEND = 'SEND',
  CONFIRM = 'CONFIRM',
  NEW_PASSWORD = 'NEW_PASSWORD',
  DONE = 'DONE',
}

type Props = {
  errorMsg?: string
} & Handler

export type Handler = {
    handleSendNewPassword: (info: ForgotPasswordRequirement) => void,
  }
  & ForgotSendHandler

type State = {
  step: Step,
} & Partial<ForgotPasswordRequirement>

type InnerHandler = {
  _handleError: any,
  _handleResend: any,
  _handleConfirm: any,
  _handleNewPassword: any,
}

type InnerProps = {
    step: Step,
  }
  & State
  & Props
  & InnerHandler

const injectHandlerEffect = withStateHandlers<State, InnerHandler, Handler>(
  () => ({step: Step.SEND}),
  {
    _handleError: (state,) => () => {
      return {
        ...state,
        step: Step.ERROR,
      }
    },
    _handleResend: (_, {handleResend}) => (email) => {
      handleResend(email)
      return {
        step: Step.CONFIRM,
        email,
      }
    },
    _handleConfirm: (state) => (code: string) => {
      return {
        ...state,
        step: Step.NEW_PASSWORD,
        code,
      }
    },
    _handleNewPassword: (state) => (password: string) => {
      return {
        ...state,
        step: Step.DONE,
        password,
      }
    },
  })

const watchStepEffect = lifecycle<InnerProps, {}>({
  componentWillReceiveProps({errorMsg, _handleError}) {
    errorMsg && _handleError()
  },
  componentWillUpdate({handleSendNewPassword, ...state}) {
    const {email, code, password} = state;
    if (email && code && password)
      handleSendNewPassword(state as ForgotPasswordRequirement)
  },
})

const StepView = ({step, errorMsg, _handleResend, _handleConfirm, _handleNewPassword}: InnerProps) => {

  switch (step) {
    case Step.ERROR:
    case Step.SEND:
      return <ConfirmCodeSendView errorMsg={errorMsg} handleResend={_handleResend}/>
    case Step.CONFIRM:
      return <CodeConfirmView handleConfirm={_handleConfirm}/>
    case Step.NEW_PASSWORD:
      return <ChangePasswordView handleChangePassword={_handleNewPassword}/>
    case Step.DONE:
      return <Redirect to={path.auth.signIn}/>
  }
}

export const ForgotPasswordStepView = compose<InnerProps, Props>(
  injectHandlerEffect,
  watchStepEffect,
  pure,
)(StepView)
