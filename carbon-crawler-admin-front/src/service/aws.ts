import {AWS} from '../config';
import Amplify from 'aws-amplify';

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
