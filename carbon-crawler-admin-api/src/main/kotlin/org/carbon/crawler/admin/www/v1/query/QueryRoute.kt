package org.carbon.crawler.admin.www.v1.query

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import org.carbon.crawler.admin.extend.ktor.receiveJsonStrict
import org.carbon.crawler.admin.usecase.query.QueryUseCase
import org.carbon.crawler.admin.www.IdRequiredError
import org.carbon.crawler.admin.www.ok
import org.carbon.crawler.admin.www.v1.PagingUrlParameter
import org.carbon.crawler.admin.www.validationError
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.validate

/**
 * @author Soda 2018/08/06.
 */
fun Route.v1Queries() {
    route("/v1/queries") {
        get {
            val params = PagingUrlParameter.get(
                call.request.queryParameters["page"],
                call.request.queryParameters["size"]
            )
            val eval = params.validate()
            when (eval) {
                is Evaluation.Accepted ->
                    call ok QueryUseCase.fetchQueries(params)
                is Evaluation.Rejected ->
                    call validationError eval
            }
        }
        post {
            val body = call.receiveJsonStrict<QueryAddRequest>()
            val eval = body.validate()
            when (eval) {
                is Evaluation.Accepted -> {
                    QueryUseCase.saveHost(body)
                    call.respond(HttpStatusCode.OK, "{}")
                }
                is Evaluation.Rejected ->
                    call validationError eval
            }
        }
    }

    route("/v1/queries/{id}") {
        put {
            val body = call.receiveJsonStrict<QueryAddRequest>()
            call.parameters["id"]
                ?.let(String::toLongOrNull)
                ?.let { id ->
                    val eval = body.validate()
                    when (eval) {
                        is Evaluation.Accepted -> {
                            QueryUseCase.updateQuery(id, body)
                            call.respond(HttpStatusCode.OK, "{}")
                        }
                        is Evaluation.Rejected ->
                            call validationError eval
                    }
                }
                ?: call.respond(HttpStatusCode.BadRequest, IdRequiredError)
        }
    }
}
