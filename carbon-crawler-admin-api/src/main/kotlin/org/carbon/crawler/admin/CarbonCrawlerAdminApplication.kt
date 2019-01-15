package org.carbon.crawler.admin

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.carbon.crawler.admin.extend.aws.cognito.CognitoConfig
import org.carbon.crawler.admin.extend.aws.cognito.CognitoJWTAuthentication
import org.carbon.crawler.admin.extend.carbon.validation.CarbonValidationModule
import org.carbon.crawler.admin.extend.hocon.ConfigLoader
import org.carbon.crawler.admin.extend.ktor.ReservedException
import org.carbon.crawler.admin.extend.ktor.auth.configure
import org.carbon.crawler.admin.feature.Exposed
import org.carbon.crawler.admin.www.v1.query.v1Queries
import org.carbon.crawler.admin.www.v1.snap.v1Snaps
import org.slf4j.LoggerFactory

/**
 * @author Soda 2018/07/22.
 */
fun Application.module() {
    // ______________________________________________________
    //
    // @ Common
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
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
    // ______________________________________________________
    //
    // @ Persist
    install(Exposed) {
        with(ConfigLoader["carbon.crawler.persist.rdb"]) {
            val host = getString("host")
            val port = getString("port")
            val schema = getString("schema")
            url = "jdbc:mysql://$host:$port/$schema"
            username = getString("user")
            password = getString("password")
            option = getObject("option").map { e -> e.key to e.value.render() }.toMap()
        }
    }
    // ______________________________________________________
    //
    // @ Error Handling
    install(StatusPages) {
        val logger = LoggerFactory.getLogger("application")

        data class ErrorMessage(val message: String)

        val iseMessage = ObjectMapper().writeValueAsString(ErrorMessage("internal server error"))
        exception<Throwable> {
            when (it) {
                is ReservedException -> call.respond(it.status, ErrorMessage(it.clientMessage))
                else -> {
                    logger.error("internal server error", it)
                    call.respondText(iseMessage, ContentType.Application.Json, HttpStatusCode.InternalServerError)
                }
            }
        }
    }
    // ______________________________________________________
    //
    // @ Security And Routing
    val enableClient = !ConfigLoader.isDefined("carbon.crawler.client.disable")
    val enableAuth = !ConfigLoader.isDefined("carbon.crawler.auth.disable")
    if (enableClient) {
        install(CORS) {
            if (enableAuth) {
                header("authorization")
            }
            method(HttpMethod.Options)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            with(ConfigLoader["carbon.crawler.client"]) {
                val host = getString("host")
                val port = getString("port")
                host("$host:$port")
            }
        }
    }
    install(Authentication) {
        if (enableAuth) {
            with(ConfigLoader["carbon.crawler.auth.cognito"]) {
                configure(CognitoJWTAuthentication, CognitoConfig(
                    getString("region"),
                    getString("poolId"),
                    getString("clientId")
                ))
            }
        }
    }
    install(Routing) {
        val routes = {
            v1Queries()
            v1Snaps()
        }
        if (enableAuth) authenticate {
            routes()
        } else {
            routes()
        }
    }
}

fun main(args: Array<String>) {
    with(ConfigLoader["carbon.crawler.server"]) {
        embeddedServer(
            Netty,
            host = getString("host"),
            watchPaths = getStringList("watch"),
            port = getInt("port"),
            module = Application::module
        ).start(true)
    }
}
