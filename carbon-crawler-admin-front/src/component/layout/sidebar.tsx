import * as React from 'react'
import styled from "styled-components"
import {Pages} from "../../route/page"
import {Link} from "react-router-dom";

export const width = '150px'

const sideBarConfig = [
  {
    title: "Dictionary",
    items: [
      {
        title: 'list',
        path: Pages.DICTIONARY_LIST,
      },
      {
        title: 'register',
        path: Pages.DICTIONARY_REGISTER
      },
    ],
  },
  {
    title: "Query",
    items: [
      {
        title: 'list',
        path: Pages.QUERY_LIST,
      },
      {
        title: 'register',
        path: Pages.QUERY_REGISTER,
      },
    ]
  }
]

type SideBarProps = {
  currentPath: string
}
export const SideBar = (props: SideBarProps) => (
  <Section>
    {(() => sideBarConfig.map((config, i) => (
      <ListSection
        key={i}
        active={config.items.map(item => item.path as string).includes(props.currentPath)}
        {...props}
        {...config}
      />
    )))()}
  </Section>
)
const Section = styled.section`
  position: fixed
  width: ${width}
  height: 100%
  background-color: #ddd
`
type ListSectionProps = {
  className?: string,
  key: number,
  currentPath: string,
  title: string,
  items: {
    title: string,
    path: Pages,
  }[],
  default?: number,
  active?: boolean,
}
const _ListSection = (props: ListSectionProps) => (
  <ul className={props.className}>
    <div>{props.title}</div>
    {(() => {
      return props.items
        .map((item, i) =>
          <li key={`listSection_${props.key}_${i}`}>
            <Link to={item.path}>
              {item.path === props.currentPath ? '>' : ''}{item.title}
            </Link>
          </li>)
    })()}
  </ul>
)
const ListSection = styled(_ListSection)`
  margin: 0
  padding: 10px
  
  background-color: ${p => !!p.active ? '#fff' : '#ddd'}
  
  a {
    text-decoration: none
    color: ${p => !!p.active ? '#aaa' : '#000'}
    display: block
  }
`
