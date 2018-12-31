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
import styled from "styled-components";

export type SignInViewProps = {
  errorMsg?: string
} & SignInHandler

export type SignInHandler = {
  handleSignIn: (info: {
    email: string,
    password: string,
  }) => void,
}

type InnerViewProps = FormBoxProps<SignInValueProps, SignInViewProps>
type SignInValueProps = {
  email: string,
  password: string,
}

const defaultProps: PickDefault<InnerViewProps> = {
  entries: {
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
  },
  buttonName: 'sign in'
}

const validationEffect = withFormik<InnerViewProps, SignInValueProps>({
  mapPropsToValues: () => ({email: '', password: ''}),
  validationSchema: Yup.object().shape({
    email: Yup.string()
      .email('illegal format, plz input email')
      .required('required: email'),
    password: Yup.string()
      .required('required: password'),
  }),
  handleSubmit: async ({email, password}, {setSubmitting, props: {handleSignIn}},) => {
    setSubmitting(true)
    handleSignIn({email, password})
  },
})

const Space = styled.span`
  width: 10px;
`

const View = (props: InnerViewProps) => (
  <>
    {props.errorMsg && <ErrorPanel text={props.errorMsg}/>}
    <FormBox {...props}/>
    <Row center>
      <PrimaryLinkButton to={path.auth.signUp}>or sign up</PrimaryLinkButton>
      <Space/>
      <SecondaryLinkButton to={path.auth.trouble.index}>any trouble?</SecondaryLinkButton>
    </Row>
  </>
)

export const SignInView = compose<InnerViewProps, SignInViewProps>(
  withProps(defaultProps),
  validationEffect,
)(View)
