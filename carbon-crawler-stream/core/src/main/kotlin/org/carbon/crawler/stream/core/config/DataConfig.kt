package org.carbon.crawler.stream.core.config

import org.jetbrains.exposed.sql.Database
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.sql.DriverManager
import javax.annotation.PostConstruct
import javax.sql.DataSource

@Import(
    DataSourceConfig::class,
    DataSourceProp::class,
    ExposedConfig::class
)
@Configuration
interface DataConfig

/**
 * [https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-kotlin.html#boot-features-kotlin-configuration-properties]
 */
@ConfigurationProperties("carbon.rdb")
class DataSourceProp {
    lateinit var rdbHost: String
    lateinit var rdbPort: String
    lateinit var rdbSchema: String
    lateinit var rdbUsername: String
    lateinit var rdbPassword: String
    var rdbOption: Map<String, String>? = null

    fun toDataSource(): DataSource {
        return org.apache.tomcat.jdbc.pool.DataSource()
            .also {
                val baseUrl = "jdbc:mysql://$rdbHost:$rdbPort/$rdbSchema"
                val option = rdbOption?.map { (key, value) -> "$key=$value" }?.joinToString("&").orEmpty()
                it.url = "$baseUrl?$option"
                it.driverClassName = DriverManager.getDriver(baseUrl)::class.qualifiedName
                it.username = rdbUsername
                it.password = rdbPassword
            }
    }
}

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
class ExposedConfig(val dataSource: DataSource) {
    @PostConstruct
    fun configureExposed() {
        Database.connect(dataSource)
    }
}
