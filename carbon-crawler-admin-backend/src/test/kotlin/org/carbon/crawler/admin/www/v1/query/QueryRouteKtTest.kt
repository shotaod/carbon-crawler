package org.carbon.crawler.admin.www.v1.query

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.shouldBe
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.carbon.composer.compose
import org.carbon.crawler.admin.KtorModuleTest
import org.carbon.crawler.admin.TestModule
import org.carbon.crawler.admin.module
import org.carbon.crawler.model.extend.composer.RollbackTransaction
import org.carbon.crawler.model.infra.record.HostTable
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.junit.jupiter.api.Test

/**
 * @author Soda 2018/08/06.
 */
class QueryRouteKtTest : KtorModuleTest {
    private val objectMapper: ObjectMapper = ObjectMapper()

    @Test
    fun get_success() = compose(TestModule(Application::module), RollbackTransaction) {
        create(HostTable)
        with(context[TestApplicationEngine::class]) {
            val call = handleRequest(HttpMethod.Get, "/v1/queries?page=1&size=10")
            call.response.status() shouldBe (HttpStatusCode.OK)
        }
    }

    @Test
    fun get_page_validation_error() = compose(TestModule(Application::module), RollbackTransaction) {
        with(context[TestApplicationEngine::class]) {
            val call = handleRequest(HttpMethod.Get, "/v1/queries?page=err")
            call.response.status() shouldBe (HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun get_size_validation_error() = compose(TestModule(Application::module), RollbackTransaction) {
        with(context[TestApplicationEngine::class]) {
            val call = handleRequest(HttpMethod.Get, "/v1/queries?size=err")
            call.response.status() shouldBe (HttpStatusCode.BadRequest)
        }
    }

    data class DictionaryPostBody(
        val url: String,
        val title: String,
        val memo: String
    )

    @Test
    fun post_url_validation_error() = compose(TestModule(Application::module), RollbackTransaction) {
        with(context[TestApplicationEngine::class]) {
            val call = handleRequest(HttpMethod.Post, "/v1/queries") {
                val body = DictionaryPostBody(
                    url = "ERR::://url.com",
                    title = "example.com",
                    memo = "this is test entry"
                )
                setBody(objectMapper.writeValueAsString(body))
            }
            call.response.status() shouldBe (HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun post_title_validation_error() = compose(TestModule(Application::module), RollbackTransaction) {
        with(context[TestApplicationEngine::class]) {
            val call = handleRequest(HttpMethod.Post, "/v1/queries") {
                val body = DictionaryPostBody(
                    url = "http://www.example.com",
                    title = "title".repeat(100),
                    memo = "this is test entry"
                )
                setBody(objectMapper.writeValueAsString(body))
            }

            call.response.status() shouldBe (HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun post_memo_validation_error() = compose(TestModule(Application::module), RollbackTransaction) {
        with(context[TestApplicationEngine::class]) {
            val call = handleRequest(HttpMethod.Post, "/v1/queries") {
                val body = DictionaryPostBody(
                    url = "http://www.example.com",
                    title = "example.com",
                    memo = "memo".repeat(500)
                )
                setBody(objectMapper.writeValueAsString(body))
            }

            call.response.status() shouldBe (HttpStatusCode.BadRequest)
        }
    }
}