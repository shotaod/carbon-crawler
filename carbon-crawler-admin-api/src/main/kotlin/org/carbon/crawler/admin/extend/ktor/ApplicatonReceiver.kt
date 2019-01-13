package org.carbon.crawler.admin.extend.ktor

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive

class JsonMappingException(e: MissingKotlinParameterException)
    : ReservedException(HttpStatusCode.BadRequest, "property missing: ${e.parameter.name}")

suspend inline fun <reified T : Any> ApplicationCall.receiveJsonStrict(): T {
    try {
        return this.receive()
    } catch (e: MissingKotlinParameterException) {
        throw JsonMappingException(e)
    }
}