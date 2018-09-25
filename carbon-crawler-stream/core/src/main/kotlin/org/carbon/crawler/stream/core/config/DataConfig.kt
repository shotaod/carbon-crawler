package org.carbon.crawler.stream.core.config

import org.jetbrains.exposed.sql.Database
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.sql.DriverManager
import javax.annotation.PostConstruct
import javax.sql.DataSource

/**
 * @author Soda 2018/07/23.
 */
@Import(
        DataSourceConfig::class,
        DataSourceProp::class,
        ExposedConfig::class
)
@Configuration
interface DataConfig

@Configuration
class DataSourceConfig constructor(
        private val prop: DataSourceProp
) {
    @Bean
    fun dataSource(): DataSource {
        return prop.toDataSource()
    }
}

@Configuration
@ConfigurationProperties("rdb.main")
class DataSourceProp : BasicDataSourceProp()

// ===================================================================================
//                                                                          ORM
//                                                                          ==========
@Configuration
class ExposedConfig(val dataSource: DataSource) {
    @PostConstruct
    fun configureExposed() {
        Database.connect(dataSource)
    }
}

/**
 * @link https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-kotlin.html#boot-features-kotlin-configuration-properties
 */
open class BasicDataSourceProp {
    lateinit var url: String
    lateinit var username: String
    lateinit var password: String
    var option: Map<String, String>? = null

    fun toDataSource(): DataSource {
        return org.apache.tomcat.jdbc.pool.DataSource().also {
            it.url = "$url${option?.map { "${it.key}=${it.value}" }?.joinToString("&", prefix = "?").orEmpty()}"
            it.driverClassName = DriverManager.getDriver(it.url)::class.qualifiedName
            it.username = username
            it.password = password
        }
    }
}