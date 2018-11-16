import {Auth} from 'aws-amplify'
import {withFormik} from 'formik';
import * as Yup from 'yup';

import * as React from 'react';
import {compose, pure, withProps} from 'recompose'

import {AsDefaultProps, DefaultInput, FormBox, FormBoxProps, PrimaryLinkButton, Row} from '../../component/parts';
import {routes} from "../../route/routes";

type Value = {
  email: string,
  password: string,
}
type FormProps = FormBoxProps<{}, Value>
type DefaultProps = AsDefaultProps<FormProps>
const defaultProps: DefaultProps = {
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

const formik = withFormik<FormProps, Value>({
  mapPropsToValues: () => ({email: '', password: ''}),
  validationSchema: Yup.object().shape({
    email: Yup.string()
      .email('illegal format, plz input email')
      .required('required: email'),
    password: Yup.string()
      .required('required: password'),
  }),
  handleSubmit: async ({email, password}, {setSubmitting}) => {
    try {
      setSubmitting(true)
      const res = await Auth.signIn(
        email,
        password,
      );
      console.log(res);
    } catch (err) {
      console.warn(err)
    } finally {
      setSubmitting(false)
    }
  },
})

const View = (props: FormProps) => (
  <>
    <FormBox {...props}/>
    <Row center>
      <PrimaryLinkButton to={routes.auth.signUp}>or sign up</PrimaryLinkButton>
    </Row>
  </>
)


export const SignInContainer = compose<FormProps, {}>(
  formik,
  withProps(defaultProps),
  pure,
)(View)
