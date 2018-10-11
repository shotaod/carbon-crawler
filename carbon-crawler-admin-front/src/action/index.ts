import * as _Auth from './auth'
import * as _Dictionary from './dictionary'
import * as _Remote from './remote'
import * as _Shared from './shared'

/*
* https://github.com/Microsoft/TypeScript/issues/4529
* should use `export import` not `export const`
*
* action.ts
* --------------------------------------------------
* import * as _Dictionary from './dictionary'
* export namespace Action {
*   export const Dictionary = _Dictionary
* }
*
* consumer.ts
* --------------------------------------------------
* import Action from 'action.ts'
*
* new Action.Dictionary.FetchRequest(...) // is ok
* Action.Dictionary.Types // compile error
*/

export namespace Action {
  export import Shared = _Shared;
  export import Remote = _Remote;

  export import Auth = _Auth;
  export import Dictionary = _Dictionary;
}
