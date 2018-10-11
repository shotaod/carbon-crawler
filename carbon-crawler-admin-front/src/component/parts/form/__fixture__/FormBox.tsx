import * as React from 'react'
import * as Yup from 'yup'
import {withFormik} from 'formik'

import {BaseProp, DefaultInput, DefaultTextarea, FormBox, FormBoxProps} from '../'
import {withName} from '../../../../__cosmos__/helper';

type Value = {
  title: string
  text: string
  url: string
  number: number
}
type ViewProps = BaseProp<Value>
type FormProps = FormBoxProps<ViewProps, Value>
const props: ViewProps = {
  entries: {
    title: {
      label: 'title',
      input: prop => (
        <DefaultInput
          {...prop}
        />
      ),
    },
    text: {
      label: 'text',
      input: prop => (
        <DefaultTextarea
          rows={3}
          {...prop}
        >
          {prop.value}
        </DefaultTextarea>
      ),
    },
    url: {
      label: 'url',
      input: prop => (
        <DefaultInput
          {...prop}
        />
      ),
    },
    number: {
      label: 'number',
      input: prop => (
        <DefaultInput
          type='number'
          {...prop}
        />
      ),
    }
  },
}

const formik = withFormik<FormProps, Value>({
  mapPropsToValues: () => ({title: '', text: '', url: '', number: 0}),
  validationSchema: Yup.object().shape({
    title: Yup.string()
      .max(16, 'input 16 characters or less')
      .required('required: title'),
    text: Yup.string()
      .max(255, 'input 255 characters or less'),
    url: Yup.string()
      .url('illegal format, plz input url')
      .required('required: url'),
    number: Yup.number()
      .min(10)
      .max(100)
      .required()
  }),
  handleSubmit: (_, {setSubmitting}) => {
    setSubmitting(true)
    setTimeout(() => {
      setSubmitting(false)
    }, 1000)
  },
})

// noinspection JSUnusedGlobalSymbols
export default {
  component: withName('parts/form/FormBox')(FormBox),
  props,
  effect: [
    formik,
  ],
};
