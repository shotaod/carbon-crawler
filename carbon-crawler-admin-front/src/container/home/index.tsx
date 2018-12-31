import * as React from 'react'
import {PrimaryLinkButton, Title} from '../../component/parts'
import {path} from '../../route/path'

export const TopContainer = () => (
  <div>
    <Title>Admin Home</Title>
    <ul>
      <li><PrimaryLinkButton to={path.dictionary.list}>list</PrimaryLinkButton></li>
      <li><PrimaryLinkButton to={path.dictionary.register}>registration</PrimaryLinkButton></li>
    </ul>
  </div>
)
