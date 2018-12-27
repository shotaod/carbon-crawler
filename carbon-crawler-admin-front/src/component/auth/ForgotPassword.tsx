import * as React from "react";
import {routes} from "../../route/routes";
import {DefaultInput, ErrorPanel, FormBox, FormBoxProps, PickDefault, Row, SecondaryLinkButton} from "../parts";
import {withFormik} from "formik";
import * as Yup from "yup";
import {compose, withProps} from "recompose";

export type ForgotPasswordProps = {
  errorMsg?: string
} & ForgotHandler

export type ForgotHandler = {
  handleResend: (email: string) => void
  handleError: (err: any) => void
}

type InnerViewProps = FormBoxProps<ValueProps, ForgotPasswordProps>
type ValueProps = {
  email: string,
}

const defaultProps: PickDefault<InnerViewProps> = {
  entries: {
    email: {
      label: 'email',
      input: prop => (
        <DefaultInput
          {...prop}
        />
      ),
    },
  },
  buttonName: 'send reset mail'
}

const validationEffect = withFormik<InnerViewProps, ValueProps>({
  mapPropsToValues: () => ({email: '', password: ''}),
  validationSchema: Yup.object().shape({
    email: Yup.string()
      .email('illegal format, plz input email')
      .required('required: email'),
  }),
  handleSubmit: async ({email}, {setSubmitting, props: {handleResend}},) => {
    setSubmitting(true)
    handleResend(email)
  },
})

const View = (props: InnerViewProps) => (
  <>
    {props.errorMsg && <ErrorPanel text={props.errorMsg}/>}
    <FormBox {...props}/>
    <Row center>
      <SecondaryLinkButton to={routes.auth.trouble.index}>{'<'} any other trouble?</SecondaryLinkButton>
    </Row>
  </>
)

export const ForgotPasswordView = compose<InnerViewProps, ForgotPasswordProps>(
  withProps(defaultProps),
  validationEffect,
)(View)
