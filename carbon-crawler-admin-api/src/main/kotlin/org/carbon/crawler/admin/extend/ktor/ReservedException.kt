package org.carbon.crawler.admin.extend.ktor

import io.ktor.http.HttpStatusCode

abstract class ReservedException(
        val status: HttpStatusCode,
        val clientMessage: String
) : Exception()
