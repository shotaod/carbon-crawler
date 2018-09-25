import * as React from 'react'
import {bindActionCreators, Dispatch} from 'redux'
import {connect} from 'react-redux'
import {compose, pure as renderOptimizeEffect} from "recompose"

import {withFormik} from "formik"
import {FormBox} from '../../../component/parts'
import * as Yup from 'yup'

import {Action} from "../../../action"
import {State} from "../../../reducer/state"
import {DefaultInput, DefaultTextarea, FormBoxProps} from "../../../component/parts/form";

// ______________________________________________________
//
// @ I/F
export type NetworkError = {
  networkCause?: string
}
export type ValidationError = {
  [K in keyof Action.Dictionary.AddForm]: string
  } & NetworkError

type ViewProps = FormBoxProps<MappedDispatch & MappedState, Action.Dictionary.AddForm>
type MappedDispatch = {
  dispatchAddDictionary: (form: Action.Dictionary.AddForm) => void
}
type MappedState = {
  validationError?: ValidationError,
} & State.Common.CallState


// ______________________________________________________
//
// @ View
const View = (props: ViewProps) => FormBox<ViewProps, Action.Dictionary.AddForm>({
  ...props,
  buttonName: 'register',
  entries: {
    title: {
      label: 'title',
      input: prop => (
        <DefaultInput
          {...prop}
          placeholder="page title..."
          type="text"
        />)
    }
    ,
    url: {
      label: 'url',
      input: prop => (
        <DefaultInput
          {...prop}
          placeholder="page url..."
          type="text"
        />)
    }
    ,
    memo: {
      label: 'memo',
      input: prop => (
        <DefaultTextarea
          {...prop}
          rows={4}
          name="memo"
          placeholder="memo..."
        >
          {prop.value}
        </DefaultTextarea>
      )
    }
  }
})

// ______________________________________________________
//
// @ Connect
const mapState = (state: State.Root): MappedState => ({
  loading: state.remote.dictionary.add.loading,
})

const mapDispatch = (dispatch: Dispatch): MappedDispatch =>
  bindActionCreators({
    dispatchAddDictionary: (addForm: Action.Dictionary.AddForm) => new Action.Dictionary.AddRequest(addForm).create()
  }, dispatch)

const connectEffect = connect(
  mapState,
  mapDispatch,
)
const validateFormEffect = withFormik<ViewProps, Action.Dictionary.AddForm>({
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
  handleSubmit: (values, {props, setSubmitting, setErrors, setError}) => {
    props.dispatchAddDictionary(values)
    setSubmitting(props.loading)
    if (props.validationError) {
      const {validationError} = props
      if (validationError.networkCause) {
        setError(validationError.networkCause)
      }
      else {
        setErrors(validationError)
      }
    }
  },
})

const effect = compose<ViewProps, {}>(
  connectEffect,
  renderOptimizeEffect,
  validateFormEffect,
)

export const DictionaryAddContainer = effect(View)
