import * as React from 'react'
import styled from 'styled-components'
import {StrokeLoader} from './Loader'
import {faLink} from "@fortawesome/free-solid-svg-icons";
import {IconLink} from "./Button";
import {RichText} from "./Text";

type Column = {
  [k in string]: string | number
}

type Item<C> = (C & { id?: number })
export type TableViewProp<C extends Column = {}> = {
  className?: string
  header: (keyof Item<C>)[]
  items: Item<C>[]
  loading: boolean
  getPath?: (id: number) => string
}

export const getTableView = <C extends Column>() => (props: TableViewProp<C>) => (
  <StyledTable>
    <thead>
    <tr>
      {props.header.map((col, i) => <th key={`table_th_${i}`}>{col}</th>)}
      <th/>
    </tr>
    </thead>

    <tbody>
    {(() => {
      if (props.loading)
        return <tr>
          <td colSpan={props.header.length + (props.getPath ? 1 : 0)}>
            <StyledStrokeLoader/>
          </td>
        </tr>

      if (props.items.length === 0)
        return <tr>
          <td colSpan={props.header.length + (props.getPath ? 1 : 0)}>
            <StyledEmpty/>
          </td>
        </tr>

      return props.items.map((item, i) => (
        <tr key={`table_tr_${i}`}>
          {props.header
            .map(k => item[k])
            .map((v, i) =>
              <td key={`table_td_${i}`}>
                <RichText text={v}/>
              </td>)
          }
          {
            props.getPath &&
            <td>
              <IconLink to={props.getPath(item.id!)} iconColor='#2e7c9c' icon={faLink}/>
            </td>
          }
        </tr>
      ))
    })()}
    </tbody>
  </StyledTable>)

const StyledTable = styled.table`
  width: 100%;
  border-collapse: collapse;

  tbody tr td {
    transition: background-color .5s, color .5s;
    background-color: #fff
  }
  tbody tr:hover td {
    //color: #fff;
    background-color: #00608814;
  }
  th {
   font-size: 1.25em;
   padding: 15px 10px;
  }
  td {
    cursor: default;
    border-top: 1px solid #ddd;
    padding: 15px 10px;
  }
`
const StyledStrokeLoader = styled(StrokeLoader)`
  margin: 0 auto;
`

const StyledEmpty = styled<any>((props: any) => <p {...props}>empty</p>)`
  text-align: center;
`
