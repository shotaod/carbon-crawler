import {compose, pure} from 'recompose'
import {connect, Dispatch} from "react-redux";
import {State} from "../../reducer/state";
import {Action} from "../../action";
import {bindActionCreators} from "redux";
import {ChangePasswordHandler, ChangePasswordView} from "../../component/page/auth";

const mapState = (state: State.Root) => ({
  errorMsg: state.auth.errorMsg,
})

const mapDispatch = (dispatch: Dispatch) => bindActionCreators<ChangePasswordHandler, ChangePasswordHandler>({
  handleChangePassword: (password: string) => new Action.Auth.ChangePassword(password).create(),
}, dispatch)


const connectEffect = connect(
  mapState,
  mapDispatch,
)

export const ChangePasswordContainer = compose<ChangePasswordHandler, {}>(
  connectEffect,
  pure,
)(ChangePasswordView)
