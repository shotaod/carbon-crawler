import * as React from "react";
import {routes} from "../../route/routes";
import {DefaultInput, FormBox, FormBoxProps, PickDefault, PrimaryLinkButton, Row} from "../parts";
import {withFormik} from "formik";

import * as Yup from "yup";
import {compose, withProps} from "recompose";
import {AuthInfo} from "../../shared";

export type SignUpViewProps = {
  errorMsg?: string
} & SignUpHandler

export type SignUpHandler = {
  handleSignUp: (signUpInfo: SignUpValues) => void
  handleError: (msg: string) => void
}
export type SignUpValues = AuthInfo & {
  username: string
  passwordConfirm: string
}

type InnerViewProps = FormBoxProps<SignUpValues, SignUpViewProps>

const defaultProps: PickDefault<InnerViewProps> = {
  entries: {
    username: {
      label: 'username',
      input: prop => (
        <DefaultInput
          {...prop}
        />
      )
    },
    email: {
      label: 'email',
      input: prop => (
        <DefaultInput
          {...prop}
        />
      ),
    },
    password: {
      label: 'password',
      input: prop => (
        <DefaultInput
          type='password'
          {...prop}
        />
      ),
    },
    passwordConfirm: {
      label: 'confirm',
      input: prop => (
        <DefaultInput
          type='password'
          {...prop}
        />
      )
    }
  },
  buttonName: 'sign up'
}

const formik = withFormik<InnerViewProps, SignUpValues>({
  mapPropsToValues: () => ({username: '', email: '', password: '', passwordConfirm: ''}),
  validationSchema: Yup.object().shape({
    username: Yup.string()
      .min(5)
      .max(16)
      .required(),
    email: Yup.string()
      .email('illegal format, plz input email')
      .required('required: email'),
    password: Yup.string()
      .min(8, 'password length must be greater than 8')
      .max(32, 'password length must be less than 32')
      .required('required: password'),
    passwordConfirm: Yup.string()
      .min(8, 'password length must be greater than 8')
      .max(32, 'password length must be less than 32')
      .oneOf([Yup.ref('password')], 'password not match')
      .required('required: password'),
  }),
  handleSubmit: async (auth, {setSubmitting, props: {handleError, handleSignUp}}) => {
    try {
      setSubmitting(true)
      handleSignUp(auth)
    } catch (err) {
      handleError(err)
    } finally {
      setSubmitting(false)
    }
  },
})

const View = (props: InnerViewProps) => (
  <>
    {props.errorMsg && (<Row>
      {props.errorMsg}
    </Row>)}
    <FormBox {...props}/>
    <Row center>
      <PrimaryLinkButton to={routes.auth.signIn}>or sign in</PrimaryLinkButton>
    </Row>
  </>
)

export const SignUpView = compose<InnerViewProps, SignUpViewProps>(
  withProps(defaultProps),
  formik,
)(View)
