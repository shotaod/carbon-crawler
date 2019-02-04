package org.carbon.crawler.stream.core.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Soda 2018/08/21.
 */
@ConfigurationProperties("trigger")
class TriggerProp : InitializingBean {
    var maxMessage: String? = null
    var cron: String? = null
    var fixedDelay: String? = null

    override fun afterPropertiesSet() {
        LoggerFactory.getLogger(TriggerProp::class.java).info(
            " {maxMessage=$maxMessage,cron=$cron,fixedDelay=$fixedDelay}"
        )
    }
}
