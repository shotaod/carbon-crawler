package org.carbon.crawler.batch.config

import org.jetbrains.exposed.sql.Database
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import java.sql.DriverManager
import javax.annotation.PostConstruct
import javax.sql.DataSource

/**
 * @author Soda 2018/07/23.
 */
@Import(
        DataSourceConfig::class,
        MainDataSourceProperty::class,
        HistoryDataSourceProperty::class,
        ExposedConfig::class
)
@Configuration
interface DataConfig

@Configuration
class DataSourceConfig constructor(
        private val mainProp: MainDataSourceProperty,
        private val historyProp: HistoryDataSourceProperty
) {
    @Bean
    @ConfigurationProperties("rdb.main")
    fun mainDataSource(): DataSource {
        return mainProp.toDataSource()
    }

    @Primary
    @Bean
    @ConfigurationProperties("rdb.history")
    fun historyDataSource(): DataSource {
        return historyProp.toDataSource()
    }
}

@Configuration
@ConfigurationProperties("rdb.main")
class MainDataSourceProperty : BasicDataSourceProp()

@Configuration
@ConfigurationProperties("rdb.history")
class HistoryDataSourceProperty : BasicDataSourceProp()

/**
 * @link https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-kotlin.html#boot-features-kotlin-configuration-properties
 */
open class BasicDataSourceProp {
    lateinit var url: String
    lateinit var username: String
    lateinit var password: String
    lateinit var option: Map<String, String>

    fun toDataSource(): DataSource {
        return org.apache.tomcat.jdbc.pool.DataSource().also {
            it.url = "$url${option.map { "${it.key}=${it.value}" }.joinToString("&", prefix = "?")}"
            it.driverClassName = DriverManager.getDriver(it.url)::class.qualifiedName
            it.username = username
            it.password = password
        }
    }
}

// ===================================================================================
//                                                                          ORM
//                                                                          ==========
@Configuration
class ExposedConfig(@Qualifier("mainDataSource") val mainDataSource: DataSource) {
    @PostConstruct
    fun configureDataBase() {
        Database.connect(mainDataSource)
    }
}
