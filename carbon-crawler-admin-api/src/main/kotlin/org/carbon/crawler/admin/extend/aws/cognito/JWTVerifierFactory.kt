package org.carbon.crawler.admin.extend.aws.cognito

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

data class CognitoConfig(
    val region: String,
    val userPoolId: String,
    val clientId: String
)

fun CognitoConfig.toVerifier(): JWTVerifier {
    val cognitoIdpUrl = "https://cognito-idp.${region}.amazonaws.com/${userPoolId}"
    val publicKeyEndpoint = ".well-known/jwks.json"

    val keyProvider = CachedPublicKeyProvider("$cognitoIdpUrl/$publicKeyEndpoint")

    return JWT.require(Algorithm.RSA256(keyProvider))
        .withAudience(clientId)
        .withIssuer(cognitoIdpUrl)
        .withClaim("token_use", "id")
        .build()
}