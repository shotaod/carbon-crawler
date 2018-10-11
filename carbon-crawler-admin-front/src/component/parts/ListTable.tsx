import * as React from 'react'
import styled from 'styled-components'
import {StrokeLoader} from './Loader'

export type ListTableProp = {
  className?: string
  header: string[]
  items: string[][]
  loading: boolean
}

//                            vvv ???
const Row = styled.div < {align? : string} > `
  justify-content: ${p => p.align || 'center'}
  display: flex
`
const _ListTable = (props: ListTableProp) => {
  return (
    <ul className={props.className}>
      <li>
        {props.header.map((col, i) => <span key={`list_table_header_${i}`}>{col}</span>)}
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
          <li key={`list_table_${i}`}>
            {item.map((col, ii) => <p key={`list_table_col_${ii}`}>{col}</p>)}
          </li>
        ))
      })()}

    </ul>
  )
}

export const ListTable = styled(_ListTable)`
  border-radius: 2px
  border: 1px solid #aaa
  box-shadow: 0px 1px 1px 0px #828282
  list-style: none
  padding: 0

  > li:first-child {
    border-radius: 2px 2px 0 0
    background-color: #2e7c9c
    color: #fff
  }
  > li {
    display: flex
    justify-content: space-around
  }
`
