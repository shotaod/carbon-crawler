package org.carbon.crawler.admin.feature

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import org.apache.tomcat.jdbc.pool.DataSource
import org.jetbrains.exposed.sql.Database
import java.sql.DriverManager

/**
 * @author Soda 2018/07/23.
 */
class Exposed private constructor(configuration: Configuration) {

    init {
        Database.connect(configuration.datasource())
    }

    class Configuration {
        var url = ""
        var username = ""
        var password = ""
        var option: Map<String, String> = HashMap()
        private fun Map<String, String>.toParam(): String =
            when {
                this.isEmpty() -> ""
                else -> this.map { e -> "${e.key}=${e.value}" }.joinToString("&", prefix = "?")
            }

        fun datasource(): DataSource = DataSource().also {
            it.url = "$url${option.toParam()}"
            it.driverClassName = DriverManager.getDriver(it.url)::class.qualifiedName
            it.username = username
            it.password = password
        }
    }

    companion object Feature : ApplicationFeature<Application, Configuration, Exposed> {
        override val key: AttributeKey<Exposed> = AttributeKey("Exposed")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Exposed {
            return Exposed(Configuration().apply(configure))
        }
    }
}
