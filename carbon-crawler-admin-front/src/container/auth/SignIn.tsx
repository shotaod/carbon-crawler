import {compose, pure} from 'recompose'
import {SignInHandler, SignInView, SignInViewProps} from "../../component/auth";
import {connect, Dispatch} from "react-redux";
import {bindActionCreators} from "redux";
import {Action} from "../../action";
import {State} from "../../reducer/state";
import {AuthInfo} from "../../shared";

const mapState = (state: State.Root) => ({
  errorMsg: state.auth.errorMsg,
})
const mapDispatch = (dispatch: Dispatch): SignInHandler => bindActionCreators<SignInHandler, SignInHandler>({
  handleSignIn: (auth: AuthInfo) => new Action.Auth.SignIn(auth).create(),
}, dispatch)

const connectEffect = connect(
  mapState,
  mapDispatch,
)

export const SignInContainer = compose<SignInViewProps, {}>(
  connectEffect,
  pure,
)(SignInView)
