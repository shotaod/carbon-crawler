package org.carbon.crawler.admin.aservice

import io.ktor.http.HttpStatusCode

abstract class UseCaseException(
        val status: HttpStatusCode,
        val clientMessage: String
) : Exception()
