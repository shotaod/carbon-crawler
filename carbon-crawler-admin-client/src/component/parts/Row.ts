import styled from 'styled-components';

export const Row = styled.div <{ center?: boolean, right?: boolean, space?: boolean }>`
  display: flex
  ${p => p.center ? 'justify-content: center' : ''}
  ${p => p.right ? 'justify-content: flex-end' : ''}
  ${p => p.space ? 'justify-content: space-between' : ''}
`
