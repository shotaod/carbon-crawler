import * as React from 'react'

type CosmosDefault = {
  onComponentRef: () => void
  onFixtureUpdate: () => void
}
type NextProxy = {
  value: React.SFC<any>
  next: () => any
}
type ProxyProps = CosmosDefault & {
  nextProxy: NextProxy
  fixture: {
    style: any
  }
}

const PureProxy: (props: ProxyProps) => JSX.Element = props => {
  const {nextProxy, ...rest} = props

  // noinspection JSUnusedLocalSymbols
  // NextProxy is actually used, but...
  const {value: NextProxy, next} = nextProxy
  const nextProxyProp = {
    ...rest,
    nextProxy: next(),
  }
  return <NextProxy {...nextProxyProp} />
}

const styleProxy = (option: any) => (props: ProxyProps) => {
  const {style} = props.fixture;
  return (
    <div style={Object.assign({}, option, style)}>
      <PureProxy {...props}/>
    </div>
  )
}

export default styleProxy
