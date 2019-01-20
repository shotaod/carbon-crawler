package org.carbon.crawler.admin.extend.cloud

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.carbon.cloud.config.standalone.CloudConfig
import org.slf4j.LoggerFactory

object ConfigLoader {
    private val logger = LoggerFactory.getLogger(ConfigLoader::class.java)
    private val cloudConfig: CloudConfig by lazy {
        val profile = System.getProperty("carbon.profile") ?: "dev"
        logger.info("profile: {}", profile)
        CloudConfig.load(profile)
    }
    private val localConfig: Config by lazy {
        val profile = System.getProperty("carbon.profile") ?: "dev"
        logger.info("profile: {}", profile)
        ConfigFactory.load().getConfig(profile)
    }

    operator fun get(prefix: String): Config = localConfig.getConfig(prefix)

    fun isDefined(boolKeyPath: String) = localConfig.hasPath(boolKeyPath) && localConfig.getBoolean(boolKeyPath)

    fun load(): CloudConfig = cloudConfig
}