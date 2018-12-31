import * as React from "react";
import {path} from "../../route/path";
import {
  DefaultInput,
  ErrorPanel,
  FormBox,
  FormBoxProps,
  PickDefault,
  PrimaryLinkButton,
  Row,
  SecondaryLinkButton
} from "../parts";
import {withFormik} from "formik";

import * as Yup from "yup";
import {compose, withProps} from "recompose";
import {AuthInfo} from "../../shared";
import styled from "styled-components";
import {validator} from "./validator";

export type SignUpViewProps = {
  errorMsg?: string
} & SignUpHandler

export type SignUpHandler = {
  handleSignUp: (signUpInfo: SignUpValues) => void
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
    password: validator.password(),
    passwordConfirm: validator.password()
      .oneOf([Yup.ref('password')], 'password not match'),
  }),
  handleSubmit: async (auth, {setSubmitting, props: {handleSignUp}}) => {
    setSubmitting(true)
    handleSignUp(auth)
  },
})

const Space = styled.span`
  width: 10px
`
const View = (props: InnerViewProps) => (
  <>
    {props.errorMsg && <ErrorPanel text={props.errorMsg}/>}
    <FormBox {...props}/>
    <Row center>
      <PrimaryLinkButton to={path.auth.signIn}>or sign in</PrimaryLinkButton>
      <Space/>
      <SecondaryLinkButton to={path.auth.trouble.index}>any trouble?</SecondaryLinkButton>
    </Row>
  </>
)

export const SignUpView = compose<InnerViewProps, SignUpViewProps>(
  withProps(defaultProps),
  formik,
)(View)
