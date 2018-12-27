export const routes = {
  home: '/',
  auth: {
    signIn: '/auth/signin',
    signUp: '/auth/signup',
    signUpResult: '/auth/signup/result',
    trouble: {
      index: '/auth/trouble/index',
      changePassword: '/auth/trouble/changepw',
      forgetPassword: '/auth/trouble/forgetpw',
      forgetPasswordConfirm: '/auth/trouble/forgetpw/confirm',
      resendConfirmMail: '/auth/trouble/resend',
    }
  },
  dictionary: {
    list: '/dictionary/list',
    register: '/dictionary/register',
  },
  query: {
    list: '/query/list',
    register: '/query/register',
  }
}
