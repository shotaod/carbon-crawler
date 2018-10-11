import {Action} from "../index";

describe('auth action test', () => {
  test('auth login action is valid', () => {
    const authLoginActionCreator = new Action.Auth.AuthLogin();
    expect(authLoginActionCreator.create()).toEqual({
      type: 'AUTH_LOGIN',
      payload: {},
      error: false,
    })
  })

  test('auth logout action is valid', () => {
    const authLoginActionCreator = new Action.Auth.AuthLogout();
    expect(authLoginActionCreator.create()).toEqual({
      type: 'AUTH_LOGOUT',
      payload: {},
      error: false,
    })
  })
});
