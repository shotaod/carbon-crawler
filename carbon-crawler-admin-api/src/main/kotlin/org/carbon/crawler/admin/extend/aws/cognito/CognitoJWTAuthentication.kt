package org.carbon.crawler.admin.extend.aws.cognito

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import org.carbon.crawler.admin.extend.ktor.auth.AuthenticationRegisterer


object CognitoJWTAuthentication : AuthenticationRegisterer<CognitoConfig> {
    override fun enable(authentication: Authentication.Configuration, param: CognitoConfig) {
        with(authentication) {
            jwt {
                realm = "carbon crawler admin api"
                verifier(param.toVerifier())
                validate {
                    JWTPrincipal(it.payload)
                }
            }
        }
    }

    private fun CognitoConfig.toVerifier(): JWTVerifier {
        val cognitoIdpUrl = "https://cognito-idp.$region.amazonaws.com/$userPoolId"
        val publicKeyEndpoint = ".well-known/jwks.json"

        val keyProvider = CachedPublicKeyProvider("$cognitoIdpUrl/$publicKeyEndpoint")

        return JWT.require(Algorithm.RSA256(keyProvider))
            .withAudience(clientId)
            .withIssuer(cognitoIdpUrl)
            .withClaim("token_use", "id")
            .build()
    }
}
