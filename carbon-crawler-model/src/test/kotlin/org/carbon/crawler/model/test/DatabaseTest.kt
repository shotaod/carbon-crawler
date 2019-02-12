package org.carbon.crawler.model.test

import org.apache.tomcat.jdbc.pool.DataSource
import org.carbon.cloud.config.standalone.CloudConfig
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll
import java.sql.DriverManager

interface DatabaseTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            // connect
            val p = CloudConfig.load("dev").dataSource
            val schemaName = "crawlerdb"
            DataSource()
                .apply {
                    url = "jdbc:mysql://${p.host}:${p.port}/crawlerdb"
                    driverClassName = DriverManager.getDriver(url)::class.qualifiedName
                    username = p.schema.getValue(schemaName).username
                    password = p.schema.getValue(schemaName).password
                }
                .also { Database.connect(it) }
        }
    }
}