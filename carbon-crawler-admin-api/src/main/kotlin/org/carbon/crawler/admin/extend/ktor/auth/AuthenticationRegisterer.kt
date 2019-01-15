package org.carbon.crawler.admin.extend.ktor.auth

import io.ktor.auth.Authentication

interface AuthenticationRegisterer<CONFIG> {
    fun enable(authentication: Authentication.Configuration, param: CONFIG)
}

fun <T> Authentication.Configuration.configure(registerer: AuthenticationRegisterer<T>, param: T) = registerer.enable(this, param)
