import * as React from "react";
import {ErrorPanel, getTableView, TableViewProp} from "../../parts";
import styled from "styled-components";
import {Card} from "../../parts/Card";
import {Model} from "../../../reducer/state";

type Summary = Omit<Model.Host, 'memo'>
export type SnapPageListViewProp = {
    errorMsg?: string
    attributes?: Model.SnapAttribute[]
  }
  & Omit<TableViewProp<Summary>, 'header'>

const SnapTable = getTableView<Summary>()
const SnapAttributeTable = getTableView<Model.SnapAttribute>()

export const SnapPageListView = (props: SnapPageListViewProp) => (
  <>
    {props.errorMsg && <ErrorPanel text={props.errorMsg}/>}
    <Container>
      <Card title='Snaps By Host'>
        <SnapTable
          header={['id', 'title', 'url']}
          {...props}
        />
      </Card>
      {props.attributes &&
      <AttributeCard title='Attributes'>
        <SnapAttributeTable
          header={['key', 'value', 'type']}
          items={props.attributes}
          loading={false}
        >
        </SnapAttributeTable>
      </AttributeCard>}
    </Container>
  </>
)

const Container = styled.div`
  padding: 20px;
  display: flex;
`

const AttributeCard = styled(Card)`
  margin-left: 20px;
  width: 30%;
`
