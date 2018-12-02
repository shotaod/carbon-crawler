package org.carbon.crawler.admin.www.v1.dictionary

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.shouldBe
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.carbon.crawler.admin.KtorModuleTest
import org.carbon.crawler.admin.module
import org.carbon.crawler.model.DictionaryTable
import org.carbon.crawler.model.extend.exposed.transactionL
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.junit.Test

/**
 * @author Soda 2018/08/06.
 */
class RouteKtTest : KtorModuleTest {
    private val objectMapper: ObjectMapper = ObjectMapper()

    @Test
    fun get_success() = withTestApplication(Application::module) {
        transactionL {
            create(DictionaryTable)
        }
        with(handleRequest(HttpMethod.Get, "/v1/dictionaries?page=1&size=10")) {
            response.status() shouldBe HttpStatusCode.OK
        }
    }

    @Test
    fun get_page_validation_error() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/v1/dictionaries?page=err")) {
            response.status() shouldBe HttpStatusCode.BadRequest
        }
    }

    @Test
    fun get_size_validation_error() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/v1/dictionaries?size=err")) {
            response.status() shouldBe HttpStatusCode.BadRequest
        }
    }

    data class DictionaryPostBody(
            val url: String,
            val title: String,
            val memo: String
    )

    @Test
    fun post_url_validation_error() = withTestApplication(Application::module) {
        val request = handleRequest(HttpMethod.Post, "/v1/dictionaries") {
            val body = DictionaryPostBody(
                    url = "ERR::://url.com",
                    title = "example.com",
                    memo = "this is test entry"
            )
            setBody(objectMapper.writeValueAsString(body))
        }
        with(request) {
            response.status() shouldBe HttpStatusCode.BadRequest
        }
    }

    @Test
    fun post_title_validation_error() = withTestApplication(Application::module) {
        val request = handleRequest(HttpMethod.Post, "/v1/dictionaries") {
            val body = DictionaryPostBody(
                    url = "http://www.example.com",
                    title = "title".repeat(100),
                    memo = "this is test entry"
            )
            setBody(objectMapper.writeValueAsString(body))
        }
        with(request) {
            response.status() shouldBe HttpStatusCode.BadRequest
        }
    }

    @Test
    fun post_memo_validation_error() = withTestApplication(Application::module) {
        val request = handleRequest(HttpMethod.Post, "/v1/dictionaries") {
            val body = DictionaryPostBody(
                    url = "http://www.example.com",
                    title = "example.com",
                    memo = "memo".repeat(500)
            )
            setBody(objectMapper.writeValueAsString(body))
        }
        with(request) {
            response.status() shouldBe HttpStatusCode.BadRequest
        }
    }
}