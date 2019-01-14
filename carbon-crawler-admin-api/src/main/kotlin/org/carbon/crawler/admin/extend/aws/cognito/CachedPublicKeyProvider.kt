package org.carbon.crawler.admin.extend.aws.cognito

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.interfaces.RSAKeyProvider
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.TimeUnit

@Suppress("FunctionName")
private fun UNSUPPORTED(msg: String): Nothing = throw UnsupportedOperationException(msg)

class CachedPublicKeyProvider(url: String) : RSAKeyProvider {
    override fun getPrivateKeyId(): String = UNSUPPORTED("this provider is only for verifying")
    override fun getPrivateKey(): RSAPrivateKey = UNSUPPORTED("this provider is only for verifying")

    private val jwkProvider: JwkProvider = JwkProviderBuilder(url)
        .cached(10/*max entries*/, 1L, TimeUnit.DAYS)
        .build()

    override fun getPublicKeyById(keyId: String?): RSAPublicKey {
        if (keyId === null) throw IllegalStateException("keyId must be specified")
        return jwkProvider[keyId].publicKey as? RSAPublicKey
            ?: throw IllegalStateException("rsa public key not found")
    }
}