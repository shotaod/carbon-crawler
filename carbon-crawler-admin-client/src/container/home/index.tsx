import * as React from 'react'
import {PrimaryLinkButton} from '../../component/parts'
import {path} from '../../route/path'

export const TopContainer = () => (
  <div>
    <h1>Admin Home</h1>
    <ul>
      <li><PrimaryLinkButton to={path.query.list}>list</PrimaryLinkButton></li>
      <li><PrimaryLinkButton to={path.query.register}>registration</PrimaryLinkButton></li>
    </ul>
  </div>
)
