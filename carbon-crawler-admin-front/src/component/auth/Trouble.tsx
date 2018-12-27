import * as React from "react";
import {Row, TextLinkButton} from "../parts";
import {routes} from "../../route/routes";

export const TroubleView = () => (
  <>
    <Row>
      <p>Any trouble with sign in?</p>
    </Row>
    <Row>
      <ul>

        <li>
          <TextLinkButton to={routes.auth.trouble.forgetPassword}>{'>'} forget password</TextLinkButton>
        </li>
        <li>
          <TextLinkButton to={routes.auth.trouble.resendConfirmMail}>{'>'} resend verification mail</TextLinkButton>
        </li>
        <li>
          <TextLinkButton to={routes.auth.signIn}>{'<'} back to sign in page</TextLinkButton>
        </li>
      </ul>
    </Row>
  </>
)
