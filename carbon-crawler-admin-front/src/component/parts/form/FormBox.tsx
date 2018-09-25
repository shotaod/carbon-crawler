import * as React from 'react'
import styled from "styled-components"
import {InjectedFormikProps} from "formik";

import {PrimaryButton} from "../Button"

// ______________________________________________________
//
// @I/F
export type FormBoxProps<Props, Values> = InjectedFormikProps<Props & BaseProp<Values>, Values>
type InputProp<Value> = {
  value: Value,
  onChange: (e: React.ChangeEvent<any>) => void,
  name: string,
}
export type BaseProp<Values> = {
  buttonName?: string
  entries: {
    [k in keyof Required<Values>]: {
    label: string,
    input: (props: InputProp<Values[k]>) => JSX.Element
  }
    }
}

// ______________________________________________________
//
// @ Component
export const FormBox = <Props, Values>(props: FormBoxProps<Props, Values>) => {
  const {entries, values, buttonName, handleSubmit, handleChange} = props;
  // optionals
  const {touched, errors} = props;

  const Rows = Object.keys(entries)
    .map(key => key as keyof Values)
    .map(key => ({key, entry: entries[key]}))
    .map((e, i) => (
      <Row key={`form_${i}`}>
        <label>{e.entry.label}</label>
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
          <PrimaryButton type="submit">
            {buttonName || 'register'}
          </PrimaryButton>
        </Row>
      </form>
    </Box>
  )
}
const Box = styled.div`
  margin: 10px 20px
`

const Row = styled.div < {center? : boolean} > `
  display: flex
  margin-bottom: 10px
  ${p => p.center ? 'justify-content: center' : ''}
  label {
    width: 100px
    text-align: right
    margin-right: 10px
  }
`

const Col = styled.div`
  width: 80%
  input, textarea {
    width: 100%
  }
`
const ErrorLabel = styled.p`
  display: inline-block
  font-size: 10px
  border-radius: 2px
  margin: 1px 0 0 0 
  padding: 5px 10px
  color: #fff
  background-color: #ff5e5e
`