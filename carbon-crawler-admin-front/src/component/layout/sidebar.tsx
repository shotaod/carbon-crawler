import * as React from 'react'
import styled from 'styled-components'
import {routes} from '../../route/routes'
import {Link} from 'react-router-dom';

export const width = '150px'

const sideBarConfig = [
  {
    title: 'Dictionary',
    items: [
      {
        title: 'list',
        path: routes.dictionary.list,
      },
      {
        title: 'register',
        path: routes.dictionary.register,
      },
    ],
  },
  {
    title: 'Query',
    items: [
      {
        title: 'list',
        path: routes.query.list,
      },
      {
        title: 'register',
        path: routes.query.register,
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
    path: string,
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
