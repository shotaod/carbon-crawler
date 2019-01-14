package org.carbon.crawler.admin.extend.ktor.auth

import com.typesafe.config.Config
import io.ktor.auth.Authentication

interface AuthenticationRegisterer {
    fun enable(authentication: Authentication.Configuration, param: Config)
}

fun Authentication.Configuration.configure(registerer: AuthenticationRegisterer, param: Config) = registerer.enable(this, param)
