import * as React from 'react'
import {PrimaryLinkButton, Title} from '../../component/parts'
import {routes} from '../../route/routes'

export const TopContainer = () => (
  <div>
    <Title>Admin Home</Title>
    <ul>
      <li><PrimaryLinkButton to={routes.dictionary.list}>list</PrimaryLinkButton></li>
      <li><PrimaryLinkButton to={routes.dictionary.register}>registration</PrimaryLinkButton></li>
    </ul>
  </div>
)
