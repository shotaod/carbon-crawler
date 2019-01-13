package org.carbon.crawler.admin.extend.hocon

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object ConfigLoader {
    private val logger = LoggerFactory.getLogger(ConfigLoader::class.java)
    private val config: Config by lazy {
        val profile = System.getProperty("carbon.profile") ?: "dev"
        logger.info("profile: {}", profile)
        ConfigFactory.load().getConfig(profile)
            .also(::println)
    }

    fun <T> withConfig(cb: Config.() -> T) = config.run(cb)
}