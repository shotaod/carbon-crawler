package org.carbon.crawler.admin.www.v1.dictionary

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.util.pipeline.PipelineContext
import org.carbon.crawler.admin.aservice.bad
import org.carbon.crawler.admin.aservice.dictionary.DictionaryAppService
import org.carbon.crawler.admin.aservice.ok
import org.carbon.crawler.admin.aservice.toItems
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.validate

/**
 * @author Soda 2018/08/06.
 */
fun Route.v1Dictionary(appService: DictionaryAppService) {
    route("/v1/dictionaries") {
        get {
            val params = GetDictionaryParameter.get(
                    call.request.queryParameters["page"],
                    call.request.queryParameters["size"]
            )
            val eval = params.validate()
            when (eval) {
                is Evaluation.Accepted ->
                    appService.fetchDictionaries(params) ok call
                is Evaluation.Rejected ->
                    respondAsValidationError(eval)
            }
        }
        post {
            val body = call.receive<PostDictionaryBody>()
            val eval = body.validate()
            when (eval) {
                is Evaluation.Accepted ->
                    appService.saveDictionary(body) ok call
                is Evaluation.Rejected ->
                    respondAsValidationError(eval)
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.respondAsValidationError(eval: Evaluation.Rejected) =
        eval.entries.toList().toItems() bad call
