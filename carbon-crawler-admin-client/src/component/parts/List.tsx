import * as React from "react";
import styled from "styled-components";

export type ListRowProp = {
  key: string,
  value: React.ReactNode,
}
export type ListProp = {
  className?: string,
  rows: ListRowProp[]
}
const _ListView = ({className, rows}: ListProp) => (
  <table className={className}>
    <tbody>
    {rows.map(({key, value}, i) => (
      <tr key={`ListView_${i}`}>
        <th>{key}</th>
        <td>:</td>
        <td>{value}</td>
      </tr>
    ))}
    </tbody>
  </table>)

export const ListView = styled(_ListView)`
  width: 100%;

  tr > th {
    text-align: left;
    padding: 10px 0;
    width: 15%;
  }

  td:nth-child(1) {
    width 10%;
  }

  td:nth-child(2) {
  }
`
