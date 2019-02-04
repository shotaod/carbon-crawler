package org.carbon.crawler.admin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.shouldBe
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.carbon.crawler.admin.test.KtorModuleTest
import org.carbon.crawler.admin.test.extend.kompose.TestModule
import org.carbon.crawler.admin.www.v1.query.DetailQuery
import org.carbon.crawler.admin.www.v1.query.ListingQuery
import org.carbon.crawler.admin.www.v1.query.QueryAddRequest
import org.carbon.crawler.model.extend.kompose.DBUtil
import org.carbon.crawler.model.extend.kompose.RollbackTransaction
import org.carbon.kompose.kompose
import org.junit.jupiter.api.Test

/**
 * @author Soda 2018/08/06.
 */
class QueryRouteKtTest : KtorModuleTest {
    @Test
    fun get_success() = kompose(TestModule(Application::module), RollbackTransaction) {
        with(context[DBUtil::class]) {
            clean()
        }
        with(context[TestApplicationEngine::class]) {
            val call = handleRequest(HttpMethod.Get, "/v1/queries?page=1&size=10")
            call.response.status() shouldBe (HttpStatusCode.OK)
        }
    }

    @Test
    fun get_page_validation_error() = kompose(TestModule(Application::module), RollbackTransaction) {
        with(context[TestApplicationEngine::class]) {
            val call = handleRequest(HttpMethod.Get, "/v1/queries?page=err")
            call.response.status() shouldBe (HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun get_size_validation_error() = kompose(TestModule(Application::module), RollbackTransaction) {
        with(context[TestApplicationEngine::class]) {
            val call = handleRequest(HttpMethod.Get, "/v1/queries?size=err")
            call.response.status() shouldBe (HttpStatusCode.BadRequest)
        }
    }

    private fun baseRequest(
        url: String = "https://example.com",
        title: String = "example.com",
        memo: String = "this is test entry",
        pagePath: String = "/list",
        linkQuery: String = "xpath://list",
        queryName: String = "headerTitle",
        query: String = "xpath:///html/h1/p",
        type: String = "text/text"
    ) = QueryAddRequest(
        url = url,
        title = title,
        memo = memo,
        listing = ListingQuery(
            pagePath = pagePath,
            linkQuery = linkQuery
        ),
        details = listOf(
            DetailQuery(
                queryName = queryName,
                query = query,
                type = type
            )
        )
    )

    @JsonIgnoreProperties("def")
    object SkipSchemaSerialize

    private val objectMapper: ObjectMapper = ObjectMapper()
        .addMixIn(QueryAddRequest::class.java, SkipSchemaSerialize::class.java)
        .addMixIn(ListingQuery::class.java, SkipSchemaSerialize::class.java)
        .addMixIn(DetailQuery::class.java, SkipSchemaSerialize::class.java)

    @Test
    fun post_validation_error() = kompose(TestModule(Application::module), RollbackTransaction) {
        // :given
        val illegalRequests = listOf(
            baseRequest(url = "ERR::///example.com"),
            baseRequest(title = "too long,".repeat(100)),
            baseRequest(memo = "too long,".repeat(1000)),
            baseRequest(pagePath = "too long,".repeat(100)),
            baseRequest(linkQuery = "ERR:://XPATH"),
            baseRequest(queryName = "too long,".repeat(100)),
            baseRequest(query = "ERR:://XPATH"),
            baseRequest(type = "UNKNOWN")
        )

        with(context[TestApplicationEngine::class]) {
            illegalRequests.forEach {
                // :when
                val body = objectMapper.writeValueAsString(it)
                println("send: \n$body")
                val call = handleRequest(HttpMethod.Post, "/v1/queries") {
                    setBody(body)
                }

                // : then
                println("receive: ${call.response.content}")
                call.response.status() shouldBe (HttpStatusCode.BadRequest)
            }
        }
    }
}