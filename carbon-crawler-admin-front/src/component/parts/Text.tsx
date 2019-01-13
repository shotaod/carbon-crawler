import styled from "styled-components";
import * as React from "react";

type RichTextProp = {
  className?: string,
  text: any,
  lines?: boolean,
  maxLength?: number
}
const _RichText = ({className, text, lines, maxLength = 100}: RichTextProp) => {
  if (typeof text !== 'string')
    return <p className={className}>{text}</p>
  if (lines) {
    return <p className={className}>
      {text.split('\n').map((line, i) => <span key={`rich_text_${i}`}>{line}<br/></span>)}
    </p>
  }

  let value = text.slice(0, maxLength - 3)
  if (text.length > maxLength) {
    value += '...'
  }
  // check protocol
  let protocol
  if (value.indexOf('://') >= 0) {
    const part = value.split('://');
    protocol = `${part[0]}://`
    value = part[1]
  }
  return (<p className={className}>
    {protocol && <Protocol>{protocol}</Protocol>}
    <span>{value}</span>
  </p>)
}
const Protocol = styled.span`
  font-weight: 800;
  color: #2e7c9c;
  padding-right: 5px;
`;
export const RichText = styled<React.StatelessComponent<RichTextProp>>(_RichText)`
  display: inline-block;
`
