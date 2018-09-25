import * as React from "react"
import {PrimaryLinkButton, Title} from "../parts"
import {Pages} from "../../route/page"

export const Top = () => (
  <div>
    <Title>Admin Home</Title>
    <ul>
      <li><PrimaryLinkButton to={Pages.DICTIONARY_LIST}>list</PrimaryLinkButton></li>
      <li><PrimaryLinkButton to={Pages.DICTIONARY_REGISTER}>registration</PrimaryLinkButton></li>
    </ul>
  </div>
)
