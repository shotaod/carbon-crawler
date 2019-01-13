package org.carbon.crawler.admin.www

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.carbon.objects.validation.evaluation.Evaluation

/**
 * @author Soda 2018/08/06.
 */
interface Json

data class Items<T>(
    val items: List<T>
) : Json

data class Pager(
    val index: Int,
    val total: Int,
    val size: Int
)

data class Page<T>(
    val items: List<T>,
    val page: Pager
) : Json

object IdRequiredError {
    val message: String get() = "id is required"
}

// ______________________________________________________
//
// @ convenience methods
fun <T> List<T>.toItems() = Items(this)

infix fun <T> List<T>.by(page: Pager) = Page(this, page)

// ______________________________________________________
//
// @ respond method

suspend infix fun ApplicationCall.ok(json: Json) = this.respond(HttpStatusCode.OK, json)
suspend infix fun ApplicationCall.bad(json: Json) = this.respond(HttpStatusCode.BadRequest, json)
suspend infix fun ApplicationCall.validationError(rejected: Evaluation.Rejected) =
    this bad rejected.entries.toList().toItems()