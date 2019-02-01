import * as _ from "lodash";
import * as React from 'react'
import {MouseEventHandler} from 'react'
import styled from 'styled-components'
import {ArrayHelpers, FieldArray, getIn, InjectedFormikProps} from 'formik';

import {ErrorLabel, IconButton, PrimaryButton, Row} from '../'
import {faTrash} from "@fortawesome/free-solid-svg-icons";
import {Card} from "../Card";
import {ListRowProp, ListView} from "../List";

// ______________________________________________________
//
// @I/F
export type FormBoxProps<Values, Additions = {}>
  = InjectedFormikProps<FormDefinition<Values> & Additions, Values>

export type PickDefault<FProps extends FormBoxProps<{}, {}>>
  = Pick<FProps, 'title' | 'entries' | 'buttonName'>

export type OmitFormik<FProps extends FormBoxProps<{}, {}>>
  = Omit<FProps, keyof InjectedFormikProps<{}, {}>>

type RenderProp<Value> = {
  value: Value,
  onChange: (e: React.ChangeEvent<any>) => void,
  name: string,
}
type Render<Type> = {
  label: string,
  render: (prop: RenderProp<Type>) => JSX.Element,
}
type Entry<Type> = ValueEntry<Type> | ObjectEntry<Type> | ArrayEntry<Type>
type ValueEntry<Type> = Render<Type>
type ObjectEntryItem = Dig<ObjectEntry<any>, 'object'>
type ObjectEntry<Type> = {
  object: {
    sectionName?: string,
    property: {
      [k in keyof Type]-?: Render<Type[k]>
    }
  }
}
type ArrayEntryItem = Dig<ArrayEntry<any>, 'array'>
type ArrayEntry<Type> = Type extends (infer T)[]
  ? {
    array: {
      renderButton: (prop: {
        onClick: MouseEventHandler<HTMLButtonElement>,
        type: string
      }) => JSX.Element,
      sectionName?: string
      property: {
        [k in keyof T]-?: Render<T[k]>
      }
    }
  }
  : never

type FormDefinition<Type> = {
  title?: string
  buttonName?: string
  entries: {
    [k in keyof Type]-?: Entry<Type[k]>
  }
}

// ______________________________________________________
//
// @ Component
// renderer
const getInputRow = (
  {
    propertyKey,
    label,
    render,
    values,
    errors,
    touched,
    submitCount,
    handleChange
  }: { propertyKey: string } & Render<{}> & FormBoxProps<{}, {}>): ListRowProp => {
  const message = getIn(errors, propertyKey)
  // https://github.com/jaredpalmer/formik/issues/738
  const isTouch = getIn(touched, propertyKey) || submitCount > 0
  return {
    key: label,
    value: <>
      {render({value: getIn(values, propertyKey), onChange: handleChange, name: propertyKey})}
      {isTouch && message && (<ErrorLabel>{message}</ErrorLabel>)}
    </>
  }
}

const InputObjectSection = (
  {
    propertyKey,
    sectionName,
    property,
    ...rest
  }: { propertyKey: string }
    & FormBoxProps<{}, {}>
    & ObjectEntryItem) => {
  const rows: ListRowProp[] = _.entries(property)
    .map(([key, propertyProp]) => {
      const nestedKey = `${propertyKey}.${key}`
      return getInputRow({
        propertyKey: nestedKey,
        ...propertyProp as Render<any>,
        ...rest,
      })
    });
  return <MarginCard title={sectionName || 'property section'}>
    <ListView rows={rows}/>
  </MarginCard>
}

const InputArraySections = (
  {
    propertyKey,
    sectionName,
    helper: {remove},
    property,
    ...rest
  }: { propertyKey: string }
    & FormBoxProps<{}, {}>
    & Omit<ArrayEntryItem, 'renderButton'>
    & { helper: ArrayHelpers }) => {
  // https://github.com/Microsoft/TypeScript/issues/14146
  // values[propertyKey] as any[]
  //                ^^^^^ is error
  const rows = (getIn(rest.values, propertyKey) as any[])
    .map((__, i) => {
      return <MarginCard
        key={`section_${i}`}
        title={`${sectionName} #${i}`}
        actionElement={<IconButton
          icon={faTrash}
          size='2x'
          iconColor='#aaa'
          onClick={(e: any) => {
            e.preventDefault()
            remove(i)
          }}
        />}
      >
        <ListView rows={
          _.entries(property)
            .map(([key, itemProp]: any[]) => {
              const indexedKey = `${propertyKey}[${i}].${key}`
              return getInputRow({
                propertyKey: indexedKey,
                ...itemProp as Render<any>,
                ...rest,
              })
            })
        }/>
      </MarginCard>
    }).flat()
  return <>{rows}</>
}
type AddButtonContainerProp = {
  className?: string,
  helper: ArrayHelpers,
  renderButton: any,
}
const _AddButtonContainer = (
  {
    className,
    helper: {push},
    renderButton
  }: AddButtonContainerProp) => (
  <Row center className={className}>
    {renderButton({
      onClick: (e: any) => {
        e.preventDefault()
        push({})
      },
      type: 'button',
    })}
  </Row>)

const AddButtonContainer = styled(_AddButtonContainer)`
  margin: 20px 0;
`;

class Renderer {
  private readonly title: string
  private readonly rows: {
    basic: ListRowProp[],
    sections: React.ReactNode[],
  } = {
    basic: [],
    sections: [],
  }

  constructor(title: string) {
    this.title = title;
  }

  pushBasic(row: ListRowProp) {
    this.rows.basic.push(row)
  }

  pushSection(el: React.ReactNode) {
    this.rows.sections.push(el)
  }

  render() {
    return (<>
      <MarginCard title={this.title}>
        <ListView rows={this.rows.basic}/>
      </MarginCard>
      {this.rows.sections}
    </>)

  }
}

export const FormBox = <Props, Values>(props: FormBoxProps<Values, Props>) => {
  const {title, entries, buttonName, handleSubmit} = props;
  // renderResult
  const renderer = new Renderer(title || 'FORM');
  const renderRows = (propertyKey: string, entry: Entry<any>) => {
    const isArray = entry.hasOwnProperty('array')
    const isObject = entry.hasOwnProperty('object')
    // if object
    if (isObject) {
      const defProp = (entry as ObjectEntry<any>).object;
      const objectInputSection = <InputObjectSection
        key={propertyKey}
        propertyKey={propertyKey}
        {...defProp}
        {...props}
      />;
      renderer.pushSection(objectInputSection)
      return
    }

    // if array
    if (isArray) {
      const {renderButton, ...rest} = (entry as ArrayEntry<any[]>).array
      const arrayInputSection = <FieldArray
        key={propertyKey}
        name={propertyKey}
        render={helper => (<>
          <InputArraySections
            propertyKey={propertyKey}
            helper={helper}
            {...rest}
            {...props}
          />
          <AddButtonContainer helper={helper} renderButton={renderButton}/>
        </>)}
      />;
      renderer.pushSection(arrayInputSection)
      return
    }

    // if single value
    const inputRow = getInputRow({
      propertyKey,
      //https://github.com/Microsoft/TypeScript/issues/10727
      ...(props as any),
      ...(entry as ValueEntry<any>),
    })
    return renderer.pushBasic(inputRow)
  }

  Object.keys(entries)
    .map(key => key as keyof Values)
    .forEach(key => renderRows(key as string, entries[key]))

  return (
    <StyledForm onSubmit={handleSubmit}>
      {renderer.render()}
      <Row center>
        <PrimaryButton type='submit'>
          {buttonName || 'submit'}
        </PrimaryButton>
      </Row>
    </StyledForm>
  )
}

const StyledForm = styled.form`
  padding: 20px;
`
const MarginCard = styled(Card)`
  margin: 20px 0;
`
