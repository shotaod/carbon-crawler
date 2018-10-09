import * as React from "react"
import {PrimaryLinkButton, Title} from "../../component/parts"
import {Pages} from "../../route/page"

export const TopContainer = () => (
  <div>
    <Title>Admin Home</Title>
    <ul>
      <li><PrimaryLinkButton to={Pages.DICTIONARY_LIST}>list</PrimaryLinkButton></li>
      <li><PrimaryLinkButton to={Pages.DICTIONARY_REGISTER}>registration</PrimaryLinkButton></li>
    </ul>
  </div>
)