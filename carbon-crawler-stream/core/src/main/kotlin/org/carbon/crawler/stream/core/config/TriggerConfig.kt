package org.carbon.crawler.stream.core.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.integration.scheduling.PollerMetadata
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.support.CronTrigger
import org.springframework.scheduling.support.PeriodicTrigger
import java.util.concurrent.TimeUnit

/**
 * @author Soda 2018/08/21.
 */
@ConfigurationProperties("carbon.trigger")
class TriggerProp {
    var maxMessage: String? = null
    var cron: String? = null
    var timeUnit: TimeUnit? = null
    var fixedDelay: String? = null
    var initialDelay: String? = null
    override fun toString(): String = """ TriggerProp(
              maxMessage=$maxMessage,
              cron=$cron,
              timeUnit=$timeUnit,
              fixedDelay=$fixedDelay,
              initialDelay=$initialDelay
           )""".trimIndent()
}

@Import(TriggerProp::class)
@Configuration
class TriggerConfig(
        val triggerProperties: TriggerProp
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TriggerConfig::class.java)
    }

    @Bean(name = [PollerMetadata.DEFAULT_POLLER])
    fun defaultPoller(trigger: Trigger): PollerMetadata = PollerMetadata().also {
        it.trigger = trigger
        it.maxMessagesPerPoll = triggerProperties.maxMessage
            ?.toLong()
            ?: throw IllegalArgumentException("Not found maxMessage property")
    }

    @Bean
    fun trigger(): Trigger {
        logger.info("{}", triggerProperties)
        val trigger =
            if (triggerProperties.cron != null) cronTrigger(triggerProperties.cron!!)
            else periodicTrigger()
        logger.info("Trigger type: ${trigger::class.simpleName}")
        return trigger
    }

    private fun cronTrigger(cron: String): Trigger {
        return CronTrigger(cron)
    }

    private fun periodicTrigger(): Trigger {
        val trigger = PeriodicTrigger(triggerProperties.fixedDelay!!.toLong(), triggerProperties.timeUnit)
        trigger.initialDelay = triggerProperties.initialDelay!!.toLong()
        return trigger
    }
}
