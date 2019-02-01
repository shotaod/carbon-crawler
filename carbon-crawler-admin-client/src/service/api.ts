import {Hosts} from "../config";

export enum HttpMethod {
  GET = 'get',
  POST = 'post',
  PUT = 'put',
  DELETE = 'delete',
}

export type Request<BODY = {}> = {
  method: HttpMethod,
  path: string,
  query?: { [key in string]: string | number },
  body?: BODY,
}

type FetchResponse = {
  result?: any,
  error?: FetchError,
}

type FetchError = {
  message: string,
}

class JsonApiCallable {

  private readonly requestUrl: string
  private readonly token: string

  constructor(requestUrl: string, token: string) {
    this.requestUrl = requestUrl
    this.token = token
  }

  private init(method: HttpMethod, param?: { [key in string]: any }): RequestInit {
    const base = {
      mode: 'cors',
      method,
      headers: new Headers({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.token}`,
      }),
    }

    return Object.assign(base, param)
  }

  private static async execute(fetch: () => Promise<Response>): Promise<FetchResponse> {
    try {
      const response = await fetch()
      const result = await response.json()

      if (response.status >= 400) {
        const {message} = result;
        return {error: {message}}
      }

      return await {result}
    } catch (cause) {
      const {message} = cause;
      return {error: {message}};
    }
  }

  get = async () =>
    await JsonApiCallable.execute(() => fetch(this.requestUrl, this.init(HttpMethod.GET)))

  post = async (body: any) =>
    await JsonApiCallable.execute(() => fetch(this.requestUrl, this.init(HttpMethod.POST, {body: JSON.stringify(body)})))

  put = async (body: any) =>
    await JsonApiCallable.execute(() => fetch(this.requestUrl, this.init(HttpMethod.PUT, {body: JSON.stringify(body)})))

  delete = async (body: any) =>
    await JsonApiCallable.execute(() => fetch(this.requestUrl, this.init(HttpMethod.DELETE, {body: JSON.stringify(body)})))
}

export class Api {
  private static toUrlParam: (param?: { [key in string]: string | number }) => string = param => {
    if (!param) return ''
    else return Object.keys(param).map(k => `${k}=${param[k]}`).join('&')
  }

  static call(request: Request, token: string): Promise<FetchResponse> {
    const {method, path, query, body} = request
    const url = `${Hosts.apiAdmin}${path}?${Api.toUrlParam(query)}`
    const apiCallable = new JsonApiCallable(url, token)
    switch (method) {
      case HttpMethod.GET:
        return apiCallable.get()
      case HttpMethod.POST:
        return apiCallable.post(body)
      case HttpMethod.PUT:
        return apiCallable.put(body)
      case HttpMethod.DELETE:
        return apiCallable.delete(body)
    }
  }
}
