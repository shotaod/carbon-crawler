import * as React from "react";
import {Row, TextLinkButton} from "../../parts";
import {path} from "../../../route/path";

export const TroublePage = () => (
  <>
    <Row>
      <p>Any trouble with sign in?</p>
    </Row>
    <Row>
      <ul>
        <li>
          <TextLinkButton to={path.auth.trouble.forgotPassword}>{'>'} forgot password</TextLinkButton>
        </li>
        <li>
          <TextLinkButton to={path.auth.trouble.resendConfirmMail}>{'>'} resend verification mail</TextLinkButton>
        </li>
        <li>
          <TextLinkButton to={path.auth.signIn}>{'<'} back to sign in page</TextLinkButton>
        </li>
      </ul>
    </Row>
  </>
)
