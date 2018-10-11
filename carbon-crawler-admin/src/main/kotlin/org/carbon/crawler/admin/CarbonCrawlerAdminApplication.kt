package org.carbon.crawler.admin

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.typesafe.config.ConfigFactory
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.config.HoconApplicationConfig
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.carbon.crawler.admin.aservice.dictionary.DictionaryAppService
import org.carbon.crawler.admin.aservice.dictionary.GetDictionarySchema
import org.carbon.crawler.admin.aservice.dictionary.PostDictionarySchema
import org.carbon.crawler.admin.aservice.validation.Validated
import org.carbon.crawler.admin.feature.installExposed
import org.carbon.crawler.admin.www.v1.dictionary

/**
 * @author Soda 2018/07/22.
 */
@Location("/v1") class V1 {
    @Location("/dictionaries") data class GetDictionary(val page: Int, val size: Int)
        : Validated<GetDictionary> by GetDictionarySchema

    @Location("/dictionaries") class PostDictionary {
        data class Body(val url: String, val title: String, val memo: String)
            : Validated<Body> by PostDictionarySchema
    }
}

fun Application.module() {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(Locations)
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        anyHost()
        host(host = "localhost:3000")
    }
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })
            registerModule(JavaTimeModule())
        }
    }
    install(Routing) {
        dictionary(DictionaryAppService())
    }
}

fun main(args: Array<String>) {
    val config = HoconApplicationConfig(ConfigFactory.load())
    installExposed(config)

    embeddedServer(Netty, 9001) {
        module()
    }.start(wait = true)
}
