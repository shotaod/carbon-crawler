import * as React from "react";
import {DefaultInput, ErrorPanel, FormBox, FormBoxProps, PickDefault} from "../../parts/index";
import {withFormik} from "formik";
import * as Yup from "yup";
import {compose, withProps} from "recompose";
import {validation} from "../../../service/validation";

export type ChangePasswordProps = {
  errorMsg?: string
} & ChangePasswordHandler

export type ChangePasswordHandler = {
  handleChangePassword: (password: string) => void
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
      render: prop => (
        <DefaultInput
          type='password'
          {...prop}
        />
      ),
    },
    confirmPassword: {
      label: 'confirm',
      render: prop => (
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
    password: validation.password(),
    confirmPassword: validation.password()
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
