import * as React from 'react';
import {Auth} from 'aws-amplify'
import {compose, pure, withProps} from 'recompose'
import {withFormik} from 'formik';
import * as Yup from 'yup';
import {AsDefaultProps, DefaultInput, FormBox, FormBoxProps} from '../../component/parts';

type Value = {
  username: string,
  email: string
  password: string
  passwordConfirm: string
}
type ViewProps = FormBoxProps<{}, Value>
const defaultProps: AsDefaultProps<ViewProps> = {
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

const formik = withFormik<ViewProps, Value>({
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
  handleSubmit: async ({email, password}, {setSubmitting}) => {
    try {
      setSubmitting(true)
      const res = await Auth.signUp({
        username: email,
        password,
        attributes: {}
      });
      console.log(res);
    } catch (err) {
      console.warn(err)
    } finally {
      setSubmitting(false)
    }
  },
})

export const SignUpContainer = compose<ViewProps, {}>(
  formik,
  withProps(defaultProps),
  pure,
)(FormBox)
