import * as React from "react";
import {Model} from "../../../reducer/state";
import {RichText} from "../../parts/Text";
import {
  DefaultInput,
  DefaultTextarea,
  EditableInput,
  EditMode,
  FormBox,
  FormBoxProps,
  PickDefault
} from "../../parts/form";
import {IconButton} from "../../parts";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import {withFormik} from "formik";
import {querySchema} from "./querySchema";
import {Message, messageHandleEffect} from "../message";
import {compose, withProps} from "recompose";
import Unwrap = Model.Shared.Unwrap;

// ______________________________________________________
//
// @ I/F
export type QueryDetailViewProp = {
    className?: string,
    item: Unwrap<Model.Query>,
    loading: boolean,
  }
  & Message
  & QueryUpdateHandler

export type QueryUpdateHandler = {
  handleUpdate: (form: Fetch.QueryAddRequest) => void,
}
type InternalViewProp = FormBoxProps<Fetch.QueryAddRequest, QueryDetailViewProp>
const validationDefProp: PickDefault<InternalViewProp> = {
  title: '',
  buttonName: 'update',
  entries: {
    id: {
      label: 'ID',
      render: ({value}) => <RichText text={value}/>
    },
    title: {
      label: 'TITLE',
      render: prop =>
        <EditableInput
          initialMode={EditMode.READ}
          read={() =>
            <RichText text={prop.value}/>}
          edit={({inputRef, onBlur}) =>
            <DefaultInput
              ref={inputRef}
              onBlur={onBlur}
              {...prop}
            />}
        />
    },
    url: {
      label: 'URL',
      render: prop => (
        <EditableInput
          initialMode={EditMode.READ}
          read={() => <RichText text={prop.value}/>}
          edit={({inputRef, onBlur}) => <DefaultInput {...prop} ref={inputRef} onBlur={onBlur}/>}
        />)
    },
    memo: {
      label: 'MEMO',
      render: props => (
        <EditableInput
          initialMode={EditMode.READ}
          read={() => <RichText lines text={props.value}/>}
          edit={({inputRef, onBlur}) => <DefaultTextarea {...props} ref={inputRef} onBlur={onBlur}/>}
        />)
    },
    listing: {
      object: {
        sectionName: 'Listing Query',
        property: {
          pagePath: {
            label: 'PATH',
            render: props => (
              <EditableInput
                initialMode={EditMode.READ}
                read={() => <RichText text={props.value}/>}
                edit={({inputRef, onBlur}) => <DefaultInput {...props} ref={inputRef} onBlur={onBlur}/>}
              />)
          },
          linkQuery: {
            label: 'LINK QUERY',
            render: props => (
              <EditableInput
                initialMode={EditMode.READ}
                read={() => <RichText text={props.value}/>}
                edit={({inputRef, onBlur}) => <DefaultInput {...props} ref={inputRef} onBlur={onBlur}/>}
              />)
          },
        },
      },
    },
    details: {
      array: {
        sectionName: 'ATTRIBUTE QUERY',
        renderButton: prop =>
          <IconButton
            icon={faPlusCircle}
            iconColor='#2e7c9c'
            size='3x'
            {...prop}
          />,
        property: {
          queryName: {
            label: 'NAME',
            render: (props) => <EditableInput
              initialMode={EditMode.READ}
              read={() => <RichText text={props.value}/>}
              edit={({inputRef, onBlur}) => <DefaultInput {...props} ref={inputRef} onBlur={onBlur}/>}
            />
          },
          query: {
            label: 'QUERY',
            render: (props) => <EditableInput
              initialMode={EditMode.READ}
              read={() => <RichText text={props.value}/>}
              edit={({inputRef, onBlur}) => <DefaultInput {...props} ref={inputRef} onBlur={onBlur}/>}
            />
          },
          type: {
            label: 'TYPE',
            render: (props) => <p>{props.value}</p>
          }
        },
      }
    }
  }
}
const messageEffect = messageHandleEffect<InternalViewProp>();
const validationEffect = withFormik<InternalViewProp, Fetch.QueryAddRequest>({
  mapPropsToValues: ({item}) => ({...item}),
  validationSchema: querySchema,
  handleSubmit: (values, {props: {handleUpdate}}) => {
    handleUpdate(values)
  }
});
const View = (props: InternalViewProp) => <FormBox {...props}/>

export const QueryDetailView = compose<InternalViewProp, QueryDetailViewProp>(
  withProps(validationDefProp),
  validationEffect,
  messageEffect,
)(View)
