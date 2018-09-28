package org.carbon.crawler.admin.feature

import io.ktor.config.HoconApplicationConfig
import org.jetbrains.exposed.sql.Database
import java.sql.DriverManager

/**
 * @author Soda 2018/07/23.
 */
fun installExposed(config: HoconApplicationConfig) {
    val url = config.property("carbon.crawler.db.url").getString()
    val username = config.property("carbon.crawler.db.username").getString()
    val password = config.property("carbon.crawler.db.password").getString()
    val option = config.property("carbon.crawler.db.option").getList()
    val dataSource = org.apache.tomcat.jdbc.pool.DataSource().also {
        it.url = "$url${option.joinToString("&", prefix = "?")}"
        it.driverClassName = DriverManager.getDriver(it.url)::class.qualifiedName
        it.username = username
        it.password = password
    }

    Database.connect(dataSource)
}
