import * as React from "react";
import {withName} from "../../../__cosmos__/helper";
import {Pager} from "../";

const NamedPager = withName('parts/Pager')((props: any) => {
  if (!props.location.search) return <Pager {...props}/>
  const {searchParams} = new URL(`http://example.com/${props.location.search}`);
  const index = parseInt(searchParams.get('index') || '', 10);
  const newProps = {
    ...props,
    index,
  }
  return <Pager {...newProps}/>
})

const getPager = (max: number) => ({
  // Don't write
  // component: withName('parts/Pager')(...
  // if do that, cosmos component tree get broken
  component: NamedPager,
  props: {
    index: 2,
    max,
    path: '/',
    size: 10,
  },
  name: `page length ${max}`,
  provideRouterProps: true,
  url: '/',
  route: '/',
})


// noinspection JSUnusedGlobalSymbols
export default [
  getPager(3),
  getPager(5),
  getPager(10),
]
