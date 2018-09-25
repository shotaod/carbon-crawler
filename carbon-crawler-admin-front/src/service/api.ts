export enum HttpMethod {
  GET = 'get',
  POST = 'post',
  PUT = 'put',
  DELETE = 'delete',
}

export type Request<BODY = {}> = {
  method: HttpMethod,
  path: string,
  query?: {[key in string]: string | number},
  body?: BODY,
}

export class JsonApiCallable {

  private requestUrl: string

  constructor(requestUrl: string) {
    this.requestUrl = requestUrl
  }

  private static init(method: HttpMethod, param?: {[key in string]: any}): RequestInit {
    const base = {
      mode: 'cors',
      method,
      headers: new Headers({
        'Content-Type': 'application/json',
      }),
    }

    return Object.assign(base, param)
  }

  private static async execute(fetch: () => Promise<Response>): Promise<{ response?: any, error?: any }> {
    try {
      const result = await fetch()
      const response = await result.json()
      return await {response}
    } catch (error) {
      return {error}
    }
  }

  get = async () =>
    await JsonApiCallable.execute(() => fetch(this.requestUrl, JsonApiCallable.init(HttpMethod.GET)))

  post = async (body: any) =>
    await JsonApiCallable.execute(() => fetch(this.requestUrl, JsonApiCallable.init(HttpMethod.POST, {body: JSON.stringify(body)})))

  put = async (body: any) =>
    await JsonApiCallable.execute(() => fetch(this.requestUrl, JsonApiCallable.init(HttpMethod.PUT, {body: JSON.stringify(body)})))

  delete = async (body: any) =>
    await JsonApiCallable.execute(() => fetch(this.requestUrl, JsonApiCallable.init(HttpMethod.DELETE, {body: JSON.stringify(body)})))
}

export class Api {
  private static readonly HOST = 'http://localhost:9001'
  private static toUrlParam: (param?: {[key in string]: string | number}) => string = param => {
    if (!param) return ''
    else return Object.keys(param).map(k => `${k}=${param[k]}`).join('&')
  }

  static call(request: Request): Promise<{ response?: any, error?: any }> {
    const {method, path, query, body} = request
    const url = `${Api.HOST}${path}?${Api.toUrlParam(query)}`
    const apiCallable = new JsonApiCallable(url)
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
