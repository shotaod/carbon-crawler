import * as React from "react";
import {DefaultInput, ErrorPanel, FormBox, FormBoxProps, PickDefault} from "../parts";
import {withFormik} from "formik";
import * as Yup from "yup";
import {compose, withProps} from "recompose";
import {validator} from "./validator";

export type ChangePasswordProps = {
  errorMsg?: string
} & ChangePasswordHandler

export type ChangePasswordHandler = {
  handleChangePassword: (email: string) => void
  handleError: (err: any) => void
}

type InnerViewProps = FormBoxProps<ValueProps, ChangePasswordProps>
type ValueProps = {
  password: string,
  confirmPassword: string,
}

const defaultProps: PickDefault<InnerViewProps> = {
  entries: {
    password: {
      label: 'new password',
      input: prop => (
        <DefaultInput
          type='password'
          {...prop}
        />
      ),
    },
    confirmPassword: {
      label: 'confirm',
      input: prop => (
        <DefaultInput
          type='password'
          {...prop}
        />
      ),
    },
  },
  buttonName: 'change password'
}

const validationEffect = withFormik<InnerViewProps, ValueProps>({
  mapPropsToValues: () => ({password: '', confirmPassword: ''}),
  validationSchema: Yup.object().shape({
    password: validator.password(),
    confirmPassword: validator.password()
      .oneOf([Yup.ref('password')], 'password not match'),
  }),
  handleSubmit: ({password}, {setSubmitting, props: {handleChangePassword}},) => {
    setSubmitting(true)
    handleChangePassword(password)
  },
})

const View = (props: InnerViewProps) => (
  <>
    {props.errorMsg && <ErrorPanel text={props.errorMsg}/>}
    <FormBox {...props}/>
  </>
)

export const ChangePasswordView = compose<InnerViewProps, ChangePasswordProps>(
  withProps(defaultProps),
  validationEffect,
)(View)
