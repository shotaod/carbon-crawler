package org.carbon.crawler.admin.www.v1.snap

import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import org.carbon.crawler.admin.usecase.snap.SnapUseCase
import org.carbon.crawler.admin.www.ok
import org.carbon.crawler.admin.www.v1.PagingUrlParameter
import org.carbon.crawler.admin.www.validationError
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.validate

fun Route.v1Snaps() {
    route("/v1/snaps") {
        get {
            val params = PagingUrlParameter.get(
                call.request.queryParameters["page"],
                call.request.queryParameters["size"]
            )
            val eval = params.validate()
            when (eval) {
                is Evaluation.Accepted ->
                    call ok SnapUseCase.fetchSnaps(params)
                is Evaluation.Rejected ->
                    call validationError eval
            }
        }
    }
}
