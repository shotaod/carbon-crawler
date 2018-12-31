import {compose, pure} from 'recompose'
import {bindActionCreators} from "redux";
import {connect, Dispatch} from "react-redux";
import {State} from "../../reducer/state";
import {Action} from "../../action";
import {ForgotPasswordStepView, Handler} from "../../component/auth/forgot";

const mapState = (state: State.Root) => ({
  errorMsg: state.auth.errorMsg,
})


const mapDispatch = (dispatch: Dispatch) => bindActionCreators<Handler, Handler>({
  handleResend: email => new Action.Auth.ForgotPasswordSendCode(email).create(),
  handleSendNewPassword: requirement => new Action.Auth.ForgotPasswordRenew(requirement).create(),
}, dispatch)

const connectEffect = connect(
  mapState,
  mapDispatch,
)

export const ForgotPasswordContainer = compose<Handler, {}>(
  connectEffect,
  pure,
)(ForgotPasswordStepView)
