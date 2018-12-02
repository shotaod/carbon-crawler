package org.carbon.crawler.admin.extend.ktor

import io.ktor.routing.Route

/**
 * @author Soda 2018/08/07.
 */
infix fun <T, P> Route.errorAware(block: suspend T.(P) -> Unit): suspend T.(P) -> Unit = {
    try {
        block(it)
    } catch (e: Throwable) {
        e.printStackTrace()
        throw e
    }
}