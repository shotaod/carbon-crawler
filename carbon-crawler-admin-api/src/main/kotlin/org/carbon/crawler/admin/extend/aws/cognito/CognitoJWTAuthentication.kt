package org.carbon.crawler.admin.extend.aws.cognito

import com.typesafe.config.Config
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import org.carbon.crawler.admin.extend.ktor.auth.AuthenticationRegisterer

object CognitoJWTAuthentication : AuthenticationRegisterer {
    override fun enable(authentication: Authentication.Configuration, param: Config) {
        with(authentication) {
            jwt {
                realm = "carbon crawler admin api"
                verifier(
                    CognitoConfig(
                        param.getString("region"),
                        param.getString("poolId"),
                        param.getString("clientId")
                    ).toVerifier()
                )
                validate {
                    JWTPrincipal(it.payload)
                }
            }
        }
    }
}
