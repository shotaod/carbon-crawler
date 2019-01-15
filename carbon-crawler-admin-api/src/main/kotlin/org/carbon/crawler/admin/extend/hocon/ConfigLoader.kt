package org.carbon.crawler.admin.extend.hocon

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object ConfigLoader {
    private val logger = LoggerFactory.getLogger(ConfigLoader::class.java)
    private val config: Config by lazy {
        val profile = System.getProperty("carbon.profile") ?: "dev"
        if (profile === "dev") {
            logger.info("profile: {}", profile)
        }
        ConfigFactory.load().getConfig(profile)
    }

    operator fun get(prefix: String): Config = config.getConfig(prefix)
    fun isDefined(boolKeyPath: String) = config.hasPath(boolKeyPath) && config.getBoolean(boolKeyPath)
}