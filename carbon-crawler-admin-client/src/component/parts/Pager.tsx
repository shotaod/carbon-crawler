import * as _ from 'lodash'
import * as React from 'react'
import styled from 'styled-components'
import {PrimaryButton} from "./Button";

/**
 * @param path: each page path to
 * @param size: fetch size
 * @param className?: https://www.styled-components.com/docs/api#caveat-with-classname
 *
 * @return element
 */
export type PagerProp = {
  className?: string,
  index: number,
  size: number,
  total: number,
  handlePageClick: (index: number) => void
}

const _Pager = (props: PagerProp) => {
  const {className, index, size, total, handlePageClick} = props
  const ClickListItem = (props: { title: string, index: number }) =>
    (<li><PrimaryButton onClick={() => handlePageClick(index)}>{props.title}</PrimaryButton></li>)
  const ListItem = (props: { title: string }) =>
    (<li><span>{props.title}</span></li>)
  const max = total / size;
  let validIndex: number
  if (index < 0) validIndex = 0
  else if (max <= index) validIndex = max - 1
  else validIndex = index

  return (
    <ul className={className}>
      {(() => {
        if (validIndex > 0) return <ClickListItem title='<' index={validIndex - 1}/>
        else return <ListItem title='<'/>
      })()}
      {(() => {
        let range
        if (validIndex < 2) range = _.range(0, _.min([5, max]))
        else if (validIndex > max - 3) range = _.range(_.max([0, max - 5]) || 0, max)
        else range = _.range(validIndex - 2, _.min([validIndex + 3, max]))
        return range
          .map(i => {
            const pageNum = i + 1
            if (i === validIndex) return (<ListItem key={`crawlQuery_${i}`} title={'' + pageNum}/>)
            else return (<ClickListItem key={`crawlQuery_${i}`} title={'' + pageNum} index={i}/>)
          })
      })()}
      {(() => {
        if (validIndex < max - 1) return <ClickListItem title='>' index={validIndex + 1}/>
        else return <ListItem title='>'/>
      })()}
    </ul>
  )
}

export const Pager = styled(_Pager)`
  > li {
    display: inline-block
    margin-left: 5px
  }
  list-style: none

  > li > a {
    cursor: pointer
    border-radius: 2px
    border: 1px solid #aaa
    color: #aaa
    padding: 10px
    border-radius: 2px
    background-color: #777
    text-decoration: none
  }

  > li > span {
    border-radius: 2px
    border: 1px solid #aaa
    color: #aaa
    padding: 10px
  }
`
