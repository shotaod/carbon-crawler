package org.carbon.crawler.admin.www.v1

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.carbon.crawler.admin.module
import org.junit.Before
import org.junit.Test

/**
 * @author Soda 2018/08/06.
 */
class DictionaryKtTest {
    lateinit var objectMapper: ObjectMapper

    @Before
    fun setUp() {
        objectMapper = ObjectMapper()
    }

    @Test
    fun dictionary_get() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/v1/dictionaries?page=10&size=10")) {
            println(response.content)
        }
    }

    data class DictionaryPostBody(
            val url: String,
            val title: String,
            val memo: String
    )

    @Test
    fun dictionary_post() = withTestApplication(Application::module) {
        val request = handleRequest(HttpMethod.Post, "/v1/dictionaries") {
            val body = DictionaryPostBody(
                    url = "http://www.example.com",
                    title = "example.com",
                    memo = "this is test entry"
            )
            this.body = objectMapper.writeValueAsString(body)
        }
        with(request) {
            println(response.content)
        }
    }
}