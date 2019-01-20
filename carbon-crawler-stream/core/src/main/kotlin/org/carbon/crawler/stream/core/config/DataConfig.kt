package org.carbon.crawler.stream.core.config

import org.carbon.cloud.config.spring.DataSourceProp
import org.jetbrains.exposed.sql.Database
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

@Import(DataSourceProp::class)
@Configuration
class DataSourceConfig constructor(
    private val p: DataSourceProp
) {
    @Bean
    fun dataSource(): DataSource {
        val schemaName = "crawlerdb"
        val crawlerSchema = p.schema[schemaName]!!
        return org.apache.tomcat.jdbc.pool.DataSource()
            .apply {
                url = "jdbc:mysql://${p.host}:${p.port}/$schemaName"
                driverClassName = DriverManager.getDriver(url)::class.qualifiedName
                username = crawlerSchema.username
                password = crawlerSchema.password
            }
    }
}

@Configuration
class ExposedConfig(val dataSource: DataSource) {
    @PostConstruct
    fun configureExposed() {
        Database.connect(dataSource)
    }
}
