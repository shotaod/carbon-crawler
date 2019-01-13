import * as React from 'react'
import styled from 'styled-components'
import {path} from '../../route/path'
import {Link, LinkProps} from 'react-router-dom';
import {faPaperclip, faSearchDollar, IconDefinition} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export const width = '240px'

const sideBarConfig = [
  {
    title: 'QUERY',
    icon: faSearchDollar,
    items: [
      {
        title: 'LIST',
        path: path.query.list,
      },
      {
        title: 'REGISTER',
        path: path.query.register,
      },
    ],
  },
  {
    title: 'SNAP',
    icon: faPaperclip,
    items: [
      {
        title: 'LIST',
        path: path.snap.list,
      },
    ]
  }
]

type SideBarProps = {
  currentPath: string
}
export const SideBar = (props: SideBarProps) => (
  <SideBarWrapper>
    <Brand>Carbon Crawler Admin</Brand>
    {(() => sideBarConfig.map((config, i) => (
      <ListSection
        key={i}
        {...props}
        {...config}
      />
    )))()}
  </SideBarWrapper>
)
const SideBarWrapper = styled.section`
  position: fixed;
  top: 0;
  width: ${width}
  height: 100vh;
  background-color: #fff
  box-shadow: 1px 0 5px 1px #afbac880;
`
type ListSectionProps = {
  className?: string,
  key: number,
  currentPath: string,
  title: string,
  icon: IconDefinition,
  items: {
    title: string,
    path: string,
  }[],
  default?: number,
}
const _ListSection = (
  {
    className,
    title,
    items,
    key,
    currentPath,
    icon
  }: ListSectionProps) => (
  <ul className={className}>
    <div>
      <FontAwesomeIcon color='#479' icon={icon} size='lg'/>
      <p>{title}</p>
    </div>
    {(() => {
      return items
        .map((item, i) =>
          <li key={`listSection_${key}_${i}`}>
            <StyledLink to={item.path} active={currentPath === item.path}>
              {item.title}
            </StyledLink>
          </li>)
    })()}
  </ul>
)
const ListSection = styled(_ListSection)`
  margin: 0;
  padding: 0 20px;
  
  > li, > div {
    margin: 20px 0;
  }

  > li {
    margin-left: 15px;
  }

  a {
    font-weight: 400;
  }

  div > svg {
    margin-right: 10px;
  }
  svg > path {
    fill: #3E728E;
  }
  div > p {
    display: inline-block;
    color: #3E728E;
    font-weight: 400;
  }
`

const _Brand = ({className}: { className?: string }) => (
  <div className={className}>
    <h1>
      <Link to={path.home}>Carbon Crawler Admin</Link>
    </h1>
    <hr/>
  </div>
)
const Brand = styled(_Brand)`
  margin: 0 15px;
  h1 {
    margin: 0;
    text-align: center;
  }
  a:visited {
    color: #3E728E;
  }
  a {
    display: inline-block;
    padding: 15px 0;
    color: #3E728E;
    font-size: 18px;
    font-weight: 200;
  }
  hr {
    color: #aaa;
    margin-top: 0;
  }
`
type CheckLinkProp = { active: boolean } & LinkProps
const LinkBase = ({children, ...rest}: LinkProps) => (<Link {...rest}>
  <p>{children}</p>
</Link>)
const StyledLink = styled<React.StatelessComponent<CheckLinkProp>>(({active, ...rest}) => <LinkBase {...rest}/>)`
  ${props => props.active ? `
  position: relative;
  display: block;
  :before {
    content: "";
    position: absolute;
    right: -20px;
    top: -9px;
    border-top: 18px solid transparent;
    border-right: 18px solid #dddddd;
    border-bottom: 18px solid transparent;
  }
  :after {
    content: "";
    position: absolute;
    right: -25px;
    top: -10px;
    border-top: 19px solid transparent;
    border-right: 19px solid #eceff2;
    border-bottom: 19px solid transparent;
  } 
  ` : ''}
`
