import * as React from 'react'
import {compose} from "recompose";

type CosmosDefault = {
  onComponentRef: () => void
  onFixtureUpdate: () => void
}
type NextProxy = {
  value: React.SFC<any>
  next: () => any
}
type Effect = (comp: React.SFC<any>) => React.ComponentClass
type ProxyProps = CosmosDefault & {
  nextProxy: NextProxy
  fixture: {
    effect: Effect | Effect[]
    props: any
  }
}

const PureProxy: (props: ProxyProps) => JSX.Element = props => {
  const {nextProxy, ...rest} = props
  const {value: NextProxy, next} = nextProxy
  const nextProxyProp = {
    ...rest,
    nextProxy: next(),
  }
  return <NextProxy {...nextProxyProp} />
}

const PropProxy: React.SFC<ProxyProps> = props => {
  const {nextProxy, fixture, ...restProp} = props
  const {onComponentRef, onFixtureUpdate, ...omit} = restProp;
  fixture.props = {
    ...fixture.props,
    ...omit,
  }
  const nextProp = {
    nextProxy,
    fixture,
    onComponentRef,
    onFixtureUpdate,
  }
  return (<PureProxy {...nextProp} />)
}

const composeProxy = () => (props: ProxyProps) => {
  const {effect} = props.fixture;
  if (!effect) return <PureProxy {...props}/>
  const Composed = Array.isArray(effect)
    ? compose<ProxyProps, {}>(...effect)(PropProxy)
    : (effect as Effect)(PropProxy);
  return <Composed {...props} />
}

export default composeProxy
