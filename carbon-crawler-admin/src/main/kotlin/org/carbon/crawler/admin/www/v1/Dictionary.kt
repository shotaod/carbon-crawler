package org.carbon.crawler.admin.www.v1

import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import org.carbon.crawler.admin.V1
import org.carbon.crawler.admin.aservice.PageRequest
import org.carbon.crawler.admin.aservice.dictionary.DictionaryAppService
import org.carbon.crawler.admin.aservice.ok
import org.carbon.crawler.admin.extend.ktor.errorAware

/**
 * @author Soda 2018/08/06.
 */
fun Route.dictionary(appService: DictionaryAppService) {
    get<V1.GetDictionary> { param ->
        val resp = appService.fetchDictionaries(PageRequest(param.page, param.size))
        call.respond(resp)
    }

    post<V1.PostDictionary>(errorAware {
        val postRequest = call.receive<V1.PostDictionary.Body>()
        val id = appService.createDictionary(postRequest)
        call.respond(id.ok())
    })
}
