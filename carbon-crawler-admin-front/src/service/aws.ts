import {AWS} from '../config';
import Amplify, {Auth} from 'aws-amplify';

export const aws = {
  configure: () => {
    Amplify.configure({
      Auth: {
        region: 'ap-northeast-1',
        userPoolId: AWS.Cognito.poolID,
        userPoolWebClientId: AWS.Cognito.clientID,
      }
    });
  }
}

export const AuthUtil = {
  jwtToken: async () => {
    const session = await Auth.currentSession()
    return session.getIdToken().getJwtToken()
  }
}
