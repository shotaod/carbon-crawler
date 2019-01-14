class Env {
  private readonly _env = process.env.NODE_ENV as string

  get value(): string {
    return this._env;
  }

  get isDev(): boolean {
    return this._env === 'development'
  }

  get isProd(): boolean {
    return this._env === 'production'
  }
}

export const env = new Env();

export const AWS = {
  Cognito: {
    poolID: 'ap-northeast-1_ol9UjYOwO',
    clientID: '3r3kp1kujo1ld5vbcla003i3di',
  }
}

export const Hosts = {
  apiAdmin: 'http://localhost:9001',
}
