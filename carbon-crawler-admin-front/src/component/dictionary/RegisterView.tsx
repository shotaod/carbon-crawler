import * as React from "react";
import {Action} from "../../action";
import {DefaultInput, DefaultTextarea, FormBox, FormBoxProps, PickDefault} from "../parts/form";
import {withFormik} from "formik";
import * as Yup from "yup";
import {compose, withProps} from "recompose";
import {AddForm} from "../../action/dictionary";
import {ErrorLabel} from "../parts";

export type DictionaryRegisterViewProps = {
  errorMsg: string,
  loading: boolean,
} & DictionaryRegisterHandler

export type DictionaryRegisterHandler = {
  handleRegister: (form: AddForm) => void,
}
type InnerViewProps = FormBoxProps<AddForm, DictionaryRegisterViewProps>
const defaultProps: PickDefault<InnerViewProps> = {
  buttonName: 'register',
  entries: {
    title: {
      label: 'title',
      input: prop => (
        <DefaultInput
          {...prop}
          placeholder='page title...'
          type='text'
        />)
    },
    url: {
      label: 'url',
      input: prop => (
        <DefaultInput
          {...prop}
          placeholder='page url...'
          type='text'
        />)
    },
    memo: {
      label: 'memo',
      input: prop => (
        <DefaultTextarea
          {...prop}
          rows={4}
          name='memo'
          placeholder='memo...'
        >
          {prop.value}
        </DefaultTextarea>
      )
    },
  },
}

const validateFormEffect = withFormik<InnerViewProps, Action.Dictionary.AddForm>({
  mapPropsToValues: () => ({title: '', url: ''}),
  validationSchema: Yup.object().shape({
    title: Yup.string()
      .max(16, 'input 16 characters or less')
      .required('required: title'),
    url: Yup.string()
      .url('illegal format, plz input url')
      .required('required: url'),
    memo: Yup.string()
      .max(255, 'input 255 characters or less'),
  }),
  handleSubmit: (values, {props, setSubmitting}) => {
    props.handleRegister(values)
    setSubmitting(true)
  },
})

const View = (props: InnerViewProps) => (
  <>
    {props.errorMsg && (<ErrorLabel>{props.errorMsg}</ErrorLabel>)}
    <FormBox {...props}/>
  </>
)

export const DictionaryRegisterView = compose<InnerViewProps, DictionaryRegisterViewProps>(
  withProps(defaultProps),
  validateFormEffect,
)(View)
