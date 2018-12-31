import * as React from 'react'
import {RefObject} from 'react'
import {PrimaryButton, Row, Title} from '../../parts'
import styled from "styled-components";
import {focusStyle} from "../../parts/form/styles";

class KeyManager {
  private readonly state: {
    ref: RefObject<HTMLInputElement>,
  }[] = [];

  private createRef = () => {
    const s = {
      ref: React.createRef<HTMLInputElement>()
    }
    this.state.push(s);
    return s.ref
  }

  private keyHandlerWithIndex = (index: number) => (e: any) => {
    // always handle input programmatically
    e.preventDefault();
    const mayNumber = parseInt(e.key);
    if (isNaN(mayNumber)) {
      return;
    }

    const ref = this.state[index].ref.current!
    ref.value = e.key
    if (index + 1 >= this.state.length) {
      ref.blur()
    } else {
      this.state[index + 1].ref.current!.focus()
    }
  }

  invokeHandler = (handler: Handler) => (e: any) => {
    e.preventDefault();
    const code = this.state.map(({ref}) => ref.current!.value).join('');
    handler(code)
  }

  toProps(i: number) {
    const {createRef, keyHandlerWithIndex} = this;
    return {
      innerRef: createRef(),
      onKeyPress: keyHandlerWithIndex(i), // handle only character (not include meta)
      onPaste: (e: any) => {
        e.preventDefault()
        const textAny = e.clipboardData.getData('text/plain') as string;
        if (!textAny) return;
        // check figure length
        if (!textAny.match(new RegExp(`\\d{${this.state.length}}`))) return;
        textAny.split('').forEach((letter, i) => {
          this.state[i].ref.current!.value = letter
        })
      },
    }
  }
}

const NumberInputBox = styled.input`
  ${focusStyle}
  border: 1px solid #ccc;
  width: 50px;
  height: 50px;
  border-radius: 2px;
  display: inline-block;
  font-size: 50px;
  vertical-align: middle;
  text-align: center;
  line-height: 50px;
  caret-color: transparent;

  margin: 0 10px;  

  ::selection {
    background-color: transparent;
  }
`

type Handler = (code: string) => void
export type ForgotConfirmHandler = {
  handleConfirm: Handler
}
export const CodeConfirmView = ({handleConfirm}: ForgotConfirmHandler) => {
  const manager = new KeyManager();
  return <div>
    <Row center>
      <Title>Check email and enter verification code</Title>
    </Row>
    <form onSubmit={manager.invokeHandler(handleConfirm)}>
      <Row center>
        <NumberInputBox {...manager.toProps(0)}/>
        <NumberInputBox {...manager.toProps(1)}/>
        <NumberInputBox {...manager.toProps(2)}/>
        <NumberInputBox {...manager.toProps(3)}/>
        <NumberInputBox {...manager.toProps(4)}/>
        <NumberInputBox {...manager.toProps(5)}/>
      </Row>
      <Row center>
        <PrimaryButton type='submit'>confirm</PrimaryButton>
      </Row>
    </form>
  </div>
}
