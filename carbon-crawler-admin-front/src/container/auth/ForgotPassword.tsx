import {compose, pure} from 'recompose'
import {ForgotHandler, ForgotPasswordView} from "../../component/auth";
import {connect, Dispatch} from "react-redux";
import {State} from "../../reducer/state";
import {Action} from "../../action";
import {bindActionCreators} from "redux";

const mapState = (state: State.Root) => ({
  errorMsg: state.auth.errorMsg,
})

const mapDispatch = (dispatch: Dispatch) => bindActionCreators<ForgotHandler, ForgotHandler>({
  handleError: (err: any) => new Action.Auth.Error(err).create(),
  handleResend: (email: string) => new Action.Auth.ForgotPassword(email).create()
}, dispatch)

const connectEffect = connect(
  mapState,
  mapDispatch,
)

export const ForgotPasswordContainer = compose<ForgotHandler, {}>(
  connectEffect,
  pure,
)(ForgotPasswordView)
