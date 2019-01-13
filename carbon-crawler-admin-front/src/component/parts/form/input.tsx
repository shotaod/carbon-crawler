import styled from 'styled-components'
import {focusStyle} from './styles'
import * as React from "react";
import {RefObject} from "react";
import {compose, StateHandler, withStateHandlers} from "recompose";
import {faPen} from "@fortawesome/free-solid-svg-icons/faPen";
import {IconButton} from "../Button";

export const DefaultInput = styled.input`
  ${focusStyle}
  height: 2.5em
  padding: 0 10px
  border: 1px solid #DDD
  border-radius: 2px
  width: 100%;
  box-sizing: border-box;
`

export const DefaultTextarea = styled.textarea`
  ${focusStyle}
  padding: 10px
  border: 1px solid #DDD
  border-radius: 2px
  resize: vertical;
  min-height: 1em;
  width: 100%;
  box-sizing: border-box;
`

type RadioProp = {
  className?: string,
  name: string,
  value?: string,
  onChange: () => void,
  candidates: {
    label: string,
    value: string,
  }[]
}
const _Radio = ({className, name, value, candidates, onChange,}: RadioProp) => (<>
  {
    candidates.map((item, i) => {
      const key = `radio_${i}`;
      return <span className={className} key={key}>
      <input id={key}
             type="radio"
             name={name}
             value={item.value}
             checked={value === item.value}
             onChange={onChange}
      />
      <label htmlFor={key}>{item.label}</label>
    </span>
    })
  }
</>)
export const DefaultRadioSelector = styled(_Radio)`
  position: relative;

  input[type=radio] {
    display: none;
  }
  input[type=radio]:checked + label:before {
    border-color: #2e7c9c;
  }
  input[type=radio]:checked + label:after {
    content: "";
    display: inline-block;
    position: absolute;
    left: 3px;
    top: -4px;
    width: 16px;
    height: 16px;
    background-color: #2e7c9c;
    border-radius: 50%;
    margin: 5px;
  }
  
  label {
    padding-left: 30px;
    margin-right: 10px;
  }
  label:before {
    content: "";
    display: inline-block;
    position: absolute;
    left: 0px;
    top: -7px;
    width: 20px;
    height: 20px;
    border: 1px solid #ddd;
    border-radius: 50%;
    margin: 5px;
  }
`

// ______________________________________________________
//
// @ EditableInput

export type EditableInputProp = {
  initialMode: EditMode
  read: React.SFC<{}>
  edit: React.SFC<{
    inputRef: React.Ref<any>,
    onBlur: (e: React.FocusEvent<any>) => void,
  }>
}

export enum EditMode {
  EDIT = 'edit',
  READ = 'read',
}

type State = {
  mode: EditMode,
  editableRef: RefObject<any>,
}
type StateHandlers = {
  changeMode: StateHandler<State>
}

type InternalProp = {
    className?: string,
  }
  & State
  & StateHandlers
  & EditableInputProp

const stateEffect = withStateHandlers<State, StateHandlers, EditableInputProp>(
  ({initialMode}) => ({mode: initialMode, editableRef: React.createRef()}),
  {
    changeMode: () => (mode: EditMode) => ({mode}),
  },
)

const _EditableInput = (
  {
    className,
    mode,
    editableRef,
    changeMode,
    ...els
  }: InternalProp) => (
  <div className={className}>
    {mode === EditMode.EDIT &&
    <span>
      {els.edit({
        inputRef: editableRef,
        onBlur: () => changeMode(EditMode.READ)
      })}
    </span>
    }

    {mode === EditMode.READ &&
    <span onClick={() => {
      changeMode(EditMode.EDIT)
      setTimeout(() => editableRef.current.focus(), 100)
    }}
    >
      {els.read({})}
      <StyledIconButton icon={faPen} iconColor='#2e7c9c' onClick={() => changeMode(EditMode.EDIT)}/>
    </span>
    }
  </div>)
const StyledIconButton = styled(IconButton)`
  margin-left: 10px;
`
export const EditableInput = compose<InternalProp, EditableInputProp>(
  stateEffect,
)(_EditableInput)
