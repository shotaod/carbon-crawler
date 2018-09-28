import * as React from "react";
import Amplify, {Auth} from 'aws-amplify'
import {compose, withProps} from 'recompose'
import {withFormik} from "formik";
import * as Yup from "yup";
import {BaseProp, DefaultInput, FormBox, FormBoxProps} from "../../component/parts";

Amplify.configure({
  Auth: {
    region: 'ap-northeast-1',
    // TODO add config file
    userPoolId: '',
    userPoolWebClientId: '',
  }
})

type Value = {
  username: string,
  email: string
  password: string
  passwordConfirm: string
}
type ViewProps = BaseProp<Value>
type FormProps = FormBoxProps<ViewProps, Value>
const props: ViewProps = {
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
          type="password"
          {...prop}
        />
      ),
    },
    passwordConfirm: {
      label: 'confirmation',
      input: prop => (
        <DefaultInput
          type="password"
          {...prop}
        />
      )
    }
  },
  buttonName: 'sign up'
}

const formik = withFormik<FormProps, Value>({
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
    console.log(email, password);
    try {
      const res = await Auth.signUp({
        username: email,
        password,
        attributes: {}
      });
      console.log(res);
    } catch (err) {
      console.warn(err)
    }

    setSubmitting(true)
    setTimeout(() => {
      setSubmitting(false)
    }, 1000)
  },
})

export const AuthenticationContainer = compose<FormProps, {}>(
  formik,
  withProps(props)
)(FormBox)
