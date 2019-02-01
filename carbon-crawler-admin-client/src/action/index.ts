import * as _Shared from './shared'
import * as _Remote from './remote'
import * as _Auth from './auth'
import * as _Query from './query'
import * as _Snap from './snap'

/*
* https://github.com/Microsoft/TypeScript/issues/4529
* should use `export import` not `export const`
*
* action.ts
* --------------------------------------------------
* import * as _Query from './query'
* export namespace Action {
*   export const Query = _Query
* }
*
* consumer.ts
* --------------------------------------------------
* import Action from 'action.ts'
*
* new Action.Query.FetchRequest(...) // is ok
* Action.Query.Types // compile error
*/

export namespace Action {
  export import Shared = _Shared;
  export import Remote = _Remote;

  export import Auth = _Auth;
  export import Query = _Query;
  export import Snap = _Snap;
}
