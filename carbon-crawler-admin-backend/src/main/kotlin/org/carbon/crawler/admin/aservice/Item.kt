package org.carbon.crawler.admin.aservice

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

/**
 * @author Soda 2018/08/06.
 */
interface Json

data class Item<T>(
        val item: T
) : Json


data class Items<T>(
        val items: List<T>
) : Json

data class Pager(
        val index: Int,
        val max: Int
)

data class Page<T>(
        val items: List<T>,
        val page: Pager
) : Json

suspend infix fun Json.ok(call: ApplicationCall) = call.respond(HttpStatusCode.OK, this)
suspend infix fun Json.bad(call: ApplicationCall) = call.respond(HttpStatusCode.BadRequest, this)


fun <T> T.toItem() = Item(this)
fun <T> List<T>.toItems() = Items(this)
infix fun <T> List<T>.by(page: Pager) = Page(this, page)