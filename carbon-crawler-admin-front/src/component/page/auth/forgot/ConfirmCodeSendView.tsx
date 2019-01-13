import * as React from "react";
import {path} from "../../../../route/path";
import {
  DefaultInput,
  ErrorPanel,
  FormBox,
  FormBoxProps,
  PickDefault,
  Row,
  SecondaryLinkButton
} from "../../../parts/index";
import {withFormik} from "formik";
import * as Yup from "yup";
import {compose, withProps} from "recompose";

export type ForgotPasswordProps = {
  errorMsg?: string
} & ForgotSendHandler

export type ForgotSendHandler = {
  handleResend: (email: string) => void
}

type InnerViewProps = FormBoxProps<ValueProps, ForgotPasswordProps>
type ValueProps = {
  email: string,
}

const defaultProps: PickDefault<InnerViewProps> = {
  entries: {
    email: {
      label: 'email',
      render: prop => (
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
  handleSubmit: ({email}, {setSubmitting, props: {handleResend}},) => {
    setSubmitting(true)
    handleResend(email)
  },
})

const View = (props: InnerViewProps) => (
  <>
    {props.errorMsg && <ErrorPanel text={props.errorMsg}/>}
    <FormBox {...props}/>
    <Row center>
      <SecondaryLinkButton to={path.auth.trouble.index}>{'<'} any other trouble?</SecondaryLinkButton>
    </Row>
  </>
)

export const ConfirmCodeSendView = compose<InnerViewProps, ForgotPasswordProps>(
  withProps(defaultProps),
  validationEffect,
)(View)
