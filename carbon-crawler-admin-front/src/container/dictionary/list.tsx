import * as _ from 'lodash'
import * as React from 'react'
import {bindActionCreators, Dispatch} from 'redux'
import {connect} from 'react-redux'
import styled from 'styled-components'

import {Model, State} from '../../reducer/state'
import {Action} from '../../action'
import {compose, lifecycle, pure as renderOptimizeEffect} from 'recompose'
import {Pager, StrokeLoader} from '../../component/parts'
import {routes} from '../../route/routes'
import {render} from '../../utils';

const PAGE_SIZE = 10

// ______________________________________________________
//
// @ I/F
type Props = MappedState & MappedDispatch

type MappedState = {
  empty: boolean
  items: ViewModel[]
  page?: Page
  loading: boolean
}

type MappedDispatch = {
  dispatchFetchDictionary: (index: number) => void
}

class Page {
  index: number
  max: number

  constructor(index: number, max: number) {
    this.index = index
    this.max = max
  }
}

class ViewModel {
  id: number
  url: string
  title: string
  memo: string

  constructor(model: Model.Dictionary) {
    this.id = model.id
    this.url = model.url
    this.title = model.title
    this.memo = model.memo
  }
}

// ______________________________________________________
//
// @ Component
const View = (props: Props) => (
  <Box>
    <h1>Dictionary List</h1>
    <ListTable {...props} />
    {render(
      props.page ?
        <Row align='flex-end'>
          <Pager
            size={PAGE_SIZE}
            path={routes.dictionary.list}
            {...props.page!}
          />
        </Row>
        : null
    )}
  </Box>
)

const Box = styled.div`
  margin: 10px 20px
`

//                            vvv ???
const Row = styled.div < {align? : string} > `
  justify-content: ${p => p.align || 'center'}
  display: flex
`

const _ListTable = (props: { className?: string, items: ViewModel[], loading: boolean }) => {
  return (
    <ul className={props.className}>
      <li>
        <span>id</span>
        <span>url</span>
        <span>title</span>
      </li>
      {(() => {
        if (props.loading) return (
          <Row>
            <StrokeLoader/>
          </Row>
        )
        if (props.items.length === 0) return (
          <li>empty</li>
        )
        return props.items.map((item, i) => (
          <li key={`component_dictionary_list_${i}`}>
            <p>{item.title}</p>
            <p>{item.url}</p>
            <p>{item.memo}</p>
          </li>
        ))
      })()}

    </ul>
  )
}

const ListTable = styled(_ListTable)`
  border-radius: 2px
  border: 1px solid #aaa
  box-shadow: 0px 1px 1px 0px #828282
  list-style: none
  padding: 0

  > li:first-child {
    border-radius: 2px 2px 0 0
    background-color: lightBlue
  }
  > li {
    display: flex
    justify-content: space-around
  }
`

// ______________________________________________________
//
// @ lifecycle
const lifecycleEffect = lifecycle<Props, {}>({
  componentWillMount() {
    const page = this.props.page
    const index = page ? page.index : 0
    this.props.dispatchFetchDictionary(index)
  },
})

// ______________________________________________________
//
// @ connect
const mapState = (state: State.Root): MappedState => {
  const {data, page} = state.dictionary
  const {loading} = state.remote.dictionary.fetch
  if (!(data && page)) {
    return {
      empty: true,
      items: [],
      loading,
    }
  }

  const items = _.values(data).map(value => new ViewModel(value))
  const search = _.get(state.router, ['location', 'search'], '') as string
  const indexParam = new URLSearchParams(search).get('index')
  const index = indexParam ? parseInt(indexParam) : 0
  const _page = new Page(index, page.max)
  return {
    empty: false,
    items,
    page: _page,
    loading,
  }
}

const mapDispatch = (dispatch: Dispatch): MappedDispatch =>
  bindActionCreators({
    dispatchFetchDictionary: (index: number) => new Action.Dictionary.FetchRequest(index, PAGE_SIZE).create()
  }, dispatch)

const connectEffect = connect(
  (store: State.Root) => mapState(store),
  (dispatch: Dispatch) => mapDispatch(dispatch)
)

const effect = compose<Props, {}>(
  connectEffect,
  renderOptimizeEffect,
  lifecycleEffect,
)

export const DictionaryListContainer = effect(View)
