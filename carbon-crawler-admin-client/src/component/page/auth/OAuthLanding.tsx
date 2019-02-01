import * as React from 'react'
import {History} from 'history';
import Auth from "@aws-amplify/auth";
import {withRouter} from "react-router";
import {path} from "../../../route/path";

const redirectProperly = async (history: History) => {

  // try {
  //   const au = await Auth.currentAuthenticatedUser()
  //   const code = new URLSearchParams(window.location.search).get('code');
  //   if (!code) throw Error('illegal state')
  //   console.log(au);
  // } catch (e) {
  //   console.warn(e)
  // }
  // try {
  //   const up = await Auth.currentUserPoolUser()
  //   console.log(up)
  // } catch (e) {
  //   console.warn(e)
  // }
  // try {
  //   const se = await Auth.currentSession()
  //   console.log(se)
  // } catch (e) {
  //   console.warn(e)
  // }

  // is login check
  try {
    const ui = await Auth.currentUserInfo()
    if (ui) history.push(path.home)
    else history.push(path.auth.signIn)
  } catch (e) {
    console.warn(e)
    history.push(path.auth.signIn)
  }
  // ______________________________________________________
  //
  // @ Federated Functions
  // try {
  //   const c = await Auth.currentCredentials()
  //   console.log(c)
  // } catch (e) {
  //   console.warn(e)
  // }
  // try {
  //   const uc = await Auth.currentUserCredentials()
  //   console.log(uc)
  // } catch (e) {
  //   console.warn(e)
  // }
}
const Component = (props: { history: History }) => {
  redirectProperly(props.history)
  return <p>redirecting...</p>
}

export const OAuthLanding = withRouter(({history}) => <Component history={history}/>)
