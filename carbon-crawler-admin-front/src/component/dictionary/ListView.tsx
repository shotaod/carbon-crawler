import {ErrorLabel, Pager, StrokeLoader} from "../parts";
import {render} from "../../utils";
import {routes} from "../../route/routes";
import styled from "styled-components";
import * as React from "react";
import {Page} from "../../shared";
import {Model} from "../../reducer/state";

type ListViewProps = {
  page?: Page,
  errorMsg?: string,
} & ListTableProp

export const ListView = (props: ListViewProps) => (
  <Box>
    {props.errorMsg && (<ErrorLabel>{props.errorMsg}</ErrorLabel>)}
    <h1>Dictionary List</h1>
    <ListTable {...props} />
    {render(
      props.page ?
        <Row align='flex-end'>
          {props.page &&
          <Pager
            getPath={i => `${routes.dictionary.list}?size=${props.page!.size}&index=${i}`}
            {...props.page!}
          />
          }
        </Row>
        : null
    )}
  </Box>
)

const Box = styled.div`
  margin: 10px 20px
`

const Row = styled.div <{ align?: string }>`
  justify-content: ${p => p.align || 'center'}
  display: flex
`

type ListTableProp = {
  className?: string,
  items: Model.Dictionary[],
  loading: boolean,
}
const _ListTable = (props: ListTableProp) => {
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
