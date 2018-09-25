package org.carbon.crawler.stream.core.config

import org.carbon.crawler.stream.core.config.TriggerProp.Companion.CRON_TRIGGER_OPTION
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase
import org.springframework.context.annotation.Import
import org.springframework.integration.scheduling.PollerMetadata
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.support.CronTrigger
import org.springframework.scheduling.support.PeriodicTrigger
import java.util.concurrent.TimeUnit

/**
 * @author Soda 2018/08/21.
 */
@ConfigurationProperties("trigger")
class TriggerProp {
    companion object {
        const val CRON_TRIGGER_OPTION = "trigger.cron"
    }

    lateinit var maxMessage: String
    var cron: String? = null
    var timeUnit: TimeUnit? = null
    var fixedDelay: String? = null
    var initialDelay: String? = null
}

const val TRIGGER_BEAN_NAME = "POLLER_TRIGGER"

@Import(TriggerProp::class)
@Configuration
class TriggerConfig(
        val triggerProperties: TriggerProp
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TriggerConfig::class.java)
    }

    @Bean(name = [PollerMetadata.DEFAULT_POLLER])
    fun defaultPoller(trigger: Trigger): PollerMetadata {
        logger.info("Trigger type: $trigger")
        val pollerMetadata = PollerMetadata()
        pollerMetadata.trigger = trigger
        pollerMetadata.maxMessagesPerPoll = this.triggerProperties.maxMessage.toLong()
        return pollerMetadata
    }

    class PeriodicTriggerCondition : NoneNestedConditions(ConfigurationPhase.REGISTER_BEAN) {
        @ConditionalOnProperty(CRON_TRIGGER_OPTION)
        class cronTrigger
    }

    @Bean(name = [TRIGGER_BEAN_NAME])
    @ConditionalOnProperty(CRON_TRIGGER_OPTION)
    fun cronTrigger(): Trigger {
        return CronTrigger(triggerProperties.cron!!)
    }

    @Bean(name = [TRIGGER_BEAN_NAME])
    @Conditional(PeriodicTriggerCondition::class)
    fun periodicTrigger(): Trigger {
        val trigger = PeriodicTrigger(triggerProperties.fixedDelay!!.toLong(), triggerProperties.timeUnit)
        trigger.initialDelay = triggerProperties.initialDelay!!.toLong()
        return trigger
    }
}