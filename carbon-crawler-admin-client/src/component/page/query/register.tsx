import * as React from "react";
import {withFormik} from "formik";
import {compose, withProps} from "recompose";
import {
  DefaultInput,
  DefaultRadioSelector,
  DefaultTextarea,
  FormBox,
  FormBoxProps,
  IconButton,
  PickDefault,
} from "../../parts/index";
import {faPlusCircle} from "@fortawesome/free-solid-svg-icons";
import {querySchema} from "./querySchema";
import {Message, messageHandleEffect} from "../message";

export type QueryRegisterViewProps = {
    loading: boolean,
  }
  & QueryRegisterHandler
  & Message

export type QueryRegisterHandler = {
  handleRegister: (form: RegisterModel) => void,
}

type RegisterModel = Omit<Fetch.QueryAddRequest, 'id'>
type InnerViewProps = FormBoxProps<RegisterModel, QueryRegisterViewProps>

const defaultProps: PickDefault<InnerViewProps> = {
  title: 'NEW QUERY',
  buttonName: 'register',
  entries: {
    title: {
      label: 'TITLE',
      render: prop => (
        <DefaultInput
          {...prop}
          placeholder='page title...'
          type='text'
        />),
    },
    url: {
      label: 'URL',
      render: prop => (
        <DefaultInput
          {...prop}
          placeholder='page url...'
          type='text'
        />)
    },
    memo: {
      label: 'MEMO',
      render: prop => (
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
    listing: {
      object: {
        sectionName: 'Listing Query',
        property: {
          pagePath: {
            label: 'PATH',
            render: props => (
              <DefaultInput
                {...props}
                placeholder='page path ...'
                type='text'
              />
            )
          },
          linkQuery: {
            label: 'LINK QUERY',
            render: props => (
              <DefaultInput
                {...props}
                placeholder='xpath://...'
                type='text'
              />
            )
          },
        }
      }
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
            render: (p: any) => (<DefaultInput
              {...p}
              placeholder='query for title...'
              type='text'
            />)
          },
          query: {
            label: 'QUERY',
            render: (p: any) => (<DefaultInput
              {...p}
              placeholder='xpath://...'
              type='text'
            />)
          },
          type: {
            label: 'TYPE',
            render: (p: any) => (<DefaultRadioSelector
              {...p}
              candidates={[
                {
                  label: 'text/text',
                  value: 'text/text',
                },
                {
                  label: 'image/text',
                  value: 'image/text',
                }
              ]}
            />)
          }
        },
      },
    },
  },
}

const initialValue: RegisterModel = {
  url: '',
  title: '',
  memo: '',
  listing: {
    pagePath: '',
    linkQuery: '',
  },
  details: [],
}
const validateFormEffect = withFormik<InnerViewProps, RegisterModel>({
  mapPropsToValues: () => initialValue,
  validationSchema: querySchema,
  handleSubmit: (values, {props: {handleRegister}}) => {
    handleRegister(values)
  },
})

const messageEffect = messageHandleEffect<InnerViewProps>({
  onSuccess: ({resetForm}) => resetForm(),
});

const View = (props: InnerViewProps) => (<FormBox {...props}/>)

export const QueryRegisterView = compose<InnerViewProps, QueryRegisterViewProps>(
  withProps(defaultProps),
  validateFormEffect,
  messageEffect,
)(View)
