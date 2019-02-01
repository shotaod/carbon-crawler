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
    region: process.env.ENV_CARBON_COGNITO_REGION,
    poolID: process.env.ENV_CARBON_COGNITO_POOL_ID,
    clientID: process.env.ENV_CARBON_COGNITO_CLIENT_ID,
  }
}

export const Hosts = {
  apiAdmin: `https://${process.env.ENV_CARBON_CLIENT_HOST}:${process.env.ENV_CARBON_CLIENT_PORT}`,
}
