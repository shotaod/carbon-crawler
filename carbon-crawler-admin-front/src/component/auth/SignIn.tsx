import * as React from "react";
import {routes} from "../../route/routes";
import {DefaultInput, FormBox, FormBoxProps, PickDefault, PrimaryLinkButton, Row} from "../parts";
import {withFormik} from "formik";
import * as Yup from "yup";
import {compose, withProps} from "recompose";

export type SignInViewProps = {
  errorMsg?: string
} & SignInHandler

export type SignInHandler = {
  handleSignIn: (info: {
    email: string,
    password: string,
  }) => void
  handleError: (err: any) => void
}

type InnerViewProps = FormBoxProps<SignInValueProps, SignInViewProps>
type SignInValueProps = {
  email: string,
  password: string,
}

const defaultProps:  PickDefault<InnerViewProps> = {
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
  handleSubmit: async ({email, password}, {setSubmitting, props: {handleError, handleSignIn}},) => {
    setSubmitting(true)
    handleSignIn({email, password})
  },
})

const View = (props: InnerViewProps) => (
  <>
    {props.errorMsg && (<Row>
      {props.errorMsg}
    </Row>)}
    <FormBox {...props}/>
    <Row center>
      <PrimaryLinkButton to={routes.auth.signUp}>or sign up</PrimaryLinkButton>
    </Row>
  </>
)

export const SignInView = compose<InnerViewProps, SignInViewProps>(
  withProps(defaultProps),
  validationEffect,
)(View)
