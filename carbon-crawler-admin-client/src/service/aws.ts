import {AWS, Hosts} from '../config'
import Amplify from '@aws-amplify/core'
import Auth from '@aws-amplify/auth'
import {path} from "../route/path";

const oauth = {
  domain: 'carbon.auth.ap-northeast-1.amazoncognito.com',
  scope: ['profile', 'email', 'openid'],
  redirectSignIn: Hosts.apiAdmin + path.auth.oauth,
  redirectSignOut: Hosts.apiAdmin + path.auth.signIn,
  // 'code' for Authorization code grant,
  // 'token' for Implicit grant
  responseType: 'code',
}
export const aws = {
  configure: () => {
    Amplify.configure({
      Auth: {
        region: AWS.Cognito.region,
        userPoolId: AWS.Cognito.poolID,
        userPoolWebClientId: AWS.Cognito.clientID,
        oauth,
      }
    })
  }
}
const {domain, redirectSignIn, responseType} = oauth;
const clientId = AWS.Cognito.clientID
const googleOauthUrl = `https://${domain}/oauth2/authorize?redirect_uri=${redirectSignIn}&response_type=${responseType}&client_id=${clientId}&identity_provider=Google`

export const AuthUtil = {
  jwtToken: async () => {
    const session = await Auth.currentSession()
    return session.getIdToken().getJwtToken()
  },
  launchGoogleOauth: () => window.location.assign(googleOauthUrl),
  shouldBeSignIn: async (elseCb: () => void) => {
    try {
      const loggedIn = await Auth.currentUserInfo();
      loggedIn || elseCb()
    } catch (e) {
      elseCb()
    }
  }
}
