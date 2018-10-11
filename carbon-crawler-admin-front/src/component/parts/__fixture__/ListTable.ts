import {ListTable} from '../ListTable';
import {withName} from '../../../__cosmos__/helper';

// noinspection JSUnusedGlobalSymbols
export default [
  {
    component: withName('parts/ListTable')(ListTable),
    props: {
      header: [
        'ID',
        'Name',
        'Age',
        'Address'
      ],
      items: [
        ['1', 'Scott', '25', 'England'],
        ['2', 'Taro', '30', 'Japan'],
        ['3', 'Taylor', '35', 'U.S.A'],
      ],
    },
  }
]
