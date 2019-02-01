import * as React from "react";
import {ErrorPanel, getTableView, Pager, PagerProp, Row, TableViewProp} from "../../parts";
import styled from "styled-components";
import {Card} from "../../parts/Card";
import {Model} from "../../../reducer/state";

type SummaryModel = Omit<Model.Host, 'memo'>;

export type SnapListViewProp = {
  errorMsg?: string
} & TableViewProp<SummaryModel>
  & PagerProp

const SnapTable = getTableView<SummaryModel>()

export const SnapListView = (props: SnapListViewProp) => (
  <>
    {props.errorMsg && <ErrorPanel text={props.errorMsg}/>}
    <Container>
      <MarginCard title='Snaps By Host'>
        <SnapTable {...props}/>
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
