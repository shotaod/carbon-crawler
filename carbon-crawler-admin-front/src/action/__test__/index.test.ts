import {Action} from "../index";

describe('auth action test', () => {
  test('auth sign in action is valid', () => {
    const authLoginActionCreator = new Action.Auth.SignIn({
      email: 'shota.oda@example.com',
      password: 'password',
    });
    expect(authLoginActionCreator.create()).toEqual({
      type: 'AUTH_SIGN_IN',
      payload: {
        email: 'shota.oda@example.com',
        password: 'password',
      },
      error: false,
    })
  })

  test('auth sign out action is valid', () => {
    const authLoginActionCreator = new Action.Auth.SignOut();
    expect(authLoginActionCreator.create()).toEqual({
      type: 'AUTH_SIGN_OUT',
      payload: {},
      error: false,
    })
  })
});
