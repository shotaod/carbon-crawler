import {compose, pure} from 'recompose'
import {SignUpHandler, SignUpValues, SignUpView, SignUpViewProps} from "../../component/page/auth";
import {connect, Dispatch} from "react-redux";
import {State} from "../../reducer/state";
import {bindActionCreators} from "redux";
import {Action} from "../../action";

const mapState = (state: State.Root) => ({
  errorMsg: state.auth.errorMsg,
})
const mapDispatch = (dispatch: Dispatch): SignUpHandler => bindActionCreators<SignUpHandler, SignUpHandler>({
  handleSignUp: (signUpInfo: SignUpValues) => new Action.Auth.SignUp(signUpInfo).create(),
}, dispatch)
const connectEffect = connect(
  mapState,
  mapDispatch,
);

export const SignUpContainer = compose<SignUpViewProps, {}>(
  connectEffect,
  pure,
)(SignUpView)
