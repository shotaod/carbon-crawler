import * as React from 'react'
import styled from 'styled-components'
import {InjectedFormikProps} from 'formik';

import {ErrorLabel, PrimaryButton, Row} from '../'

// ______________________________________________________
//
// @I/F
export type FormBoxProps<Values, Additions = {}>
  = InjectedFormikProps<Additions & AsForm<Values>, Values>

export type PickDefault<FProps extends FormBoxProps<{}, {}>>
  = Pick<FProps, 'entries'>
  & Pick<FProps, 'buttonName'>

export type OmitFormik<FProps extends FormBoxProps<{}, {}>>
  = Omit<FProps, keyof InjectedFormikProps<{}, {}>>

type AsInput<Value> = {
  value: Value,
  onChange: (e: React.ChangeEvent<any>) => void,
  name: string,
}
type AsForm<Values> = {
  buttonName?: string
  entries: {
    [k in keyof Required<Values>]: {
      label: string,
      input: (props: AsInput<Values[k]>) => JSX.Element
    }
  }
}

// ______________________________________________________
//
// @ Component
export const FormBox = <Props, Values>(props: FormBoxProps<Values, Props>) => {
  const {entries, values, buttonName, handleSubmit, handleChange} = props;
  // optionals
  const {touched, errors} = props;

  const Rows = Object.keys(entries)
    .map(key => key as keyof Values)
    .map(key => ({key, entry: entries[key]}))
    .map((e, i) => (
      <Row key={`form_${i}`}>
        <Label>{e.entry.label}</Label>
        <Col>
          {e.entry.input({value: values[e.key], onChange: handleChange, name: '' + e.key})}
          {touched && errors && touched[e.key] && errors[e.key] && <ErrorLabel>{errors[e.key]}</ErrorLabel>}
        </Col>
      </Row>
    ))

  return (
    <Box>
      <form onSubmit={handleSubmit}>
        {Rows}
        <Row center>
          <PrimaryButton type='submit'>
            {buttonName || 'submit'}
          </PrimaryButton>
        </Row>
      </form>
    </Box>
  )
}
const Box = styled.div`
  margin: 10px 20px
`
const Col = styled.div`
  width: 80%
  input, textarea {
    width: 100%
  }
`

const Label = styled.label`
  width: 100px;
  text-align: right;
  margin-right: 10px;
`
