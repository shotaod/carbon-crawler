import * as React from "react";
import {ErrorPanel, getTableView, Pager, PagerProp, Row, TableViewProp} from "../../parts";
import styled from "styled-components";
import {Card} from "../../parts/Card";
import {Model} from "../../../reducer/state";

type SummaryModel = Omit<Model.Host, 'memo'>
export type QueryListViewProp = {
  errorMsg?: string
} & TableViewProp<SummaryModel>
  & PagerProp

const QueryTable = getTableView<SummaryModel>()

export const QueryListView = (props: QueryListViewProp) => (
  <>
    {props.errorMsg && <ErrorPanel text={props.errorMsg}/>}
    <Container>
      <MarginCard title='Queries By Host'>
        <QueryTable {...props}/>
        <Row right>
          <Pager {...props}/>
        </Row>
      </MarginCard>
    </Container>
  </>
)

const Container = styled.div`
  padding: 10px;
`

const MarginCard = styled(Card)`
  margin: 20px;
`
