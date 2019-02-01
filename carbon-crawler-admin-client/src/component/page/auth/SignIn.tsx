import * as React from "react";
import {path} from "../../../route/path";
import {
  DefaultInput,
  ErrorPanel,
  FormBox,
  FormBoxProps,
  PickDefault,
  PlainButton,
  PrimaryLinkButton,
  Row,
  SecondaryLinkButton
} from "../../parts/index";
import {withFormik} from "formik";
import * as Yup from "yup";
import {compose, withProps} from "recompose";
import styled from "styled-components";
import {GoogleIcon} from "./GoogleIcon";
import {AuthUtil} from "../../../service/aws";

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
      render: prop => (
        <DefaultInput
          {...prop}
        />
      ),
    },
    password: {
      label: 'password',
      render: prop => (
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

const _GoogleLoginButton = (prop: { className?: string }) =>
  <PlainButton
    {...prop}
    onClick={AuthUtil.launchGoogleOauth}
  >
    <div>
      <GoogleIcon/>
    </div>
  </PlainButton>

const GoogleLoginButton = styled(_GoogleLoginButton)`
  background-color: #2e7c9c;

  div {
    width: 22px;
    height: 22px;
  }
  div svg {
    padding: 2px;
    border-radius: 50%;
    background-color: #fff;
  }
`

const View = (props: InnerViewProps) => (
  <>
    {props.errorMsg && <ErrorPanel text={props.errorMsg}/>}
    <FormBox {...props}/>
    <Row center>
      <GoogleLoginButton/>
      <Space/>
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
