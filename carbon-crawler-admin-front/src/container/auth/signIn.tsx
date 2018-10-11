import * as React from 'react';
import {Auth} from 'aws-amplify'
import {compose, pure as renderOptimizeEffect, withProps} from 'recompose'
import {withFormik} from 'formik';
import * as Yup from 'yup';
import {BaseProp, DefaultInput, FormBox, FormBoxProps} from '../../component/parts';

type Value = {
  email: string
  password: string
}
type ViewProps = BaseProp<Value>
type FormProps = FormBoxProps<ViewProps, Value>
const props: ViewProps = {
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

export const SignInContainer = compose<FormProps, {}>(
  formik,
  withProps(props),
  renderOptimizeEffect
)(FormBox)
