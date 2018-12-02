package org.carbon.crawler.admin

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.typesafe.config.ConfigFactory
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.carbon.crawler.admin.aservice.dictionary.DictionaryAppService
import org.carbon.crawler.admin.extend.carbon.validation.CarbonValidationModule
import org.carbon.crawler.admin.feature.Exposed
import org.carbon.crawler.admin.www.v1.dictionary.v1Dictionary

/**
 * @author Soda 2018/07/22.
 */
fun Application.module() {
    install(Exposed) {
        ConfigFactory.load()
                .apply {
                    url = getString("carbon.crawler.db.url")
                    username = getString("carbon.crawler.db.username")
                    password = getString("carbon.crawler.db.password")
                    option = getObject("carbon.crawler.db.option").map { e -> e.key to e.value.render() }.toMap()
                }
    }

    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
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
            registerModule(CarbonValidationModule)
            registerModule(JavaTimeModule())
        }
    }
    install(Routing) {
        v1Dictionary(DictionaryAppService())
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, 9001) {
        module()
    }.start(wait = true)
}
