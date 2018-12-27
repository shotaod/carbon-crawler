import styled from 'styled-components';

export const Row = styled.div < {center? : boolean} > `
  display: flex
  margin-bottom: 10px
  ${p => p.center ? 'justify-content: center' : ''}
`
