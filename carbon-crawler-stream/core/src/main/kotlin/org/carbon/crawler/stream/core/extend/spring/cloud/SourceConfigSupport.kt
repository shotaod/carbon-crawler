package org.carbon.crawler.stream.core.extend.spring.cloud

import org.springframework.cloud.stream.messaging.Source
import org.springframework.integration.annotation.InboundChannelAdapter
import org.springframework.integration.annotation.Poller

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@InboundChannelAdapter(
    value = Source.OUTPUT,
    poller = [Poller(
        maxMessagesPerPoll = "\${trigger.maxMessage:}",
        fixedDelay = "\${trigger.fixedDelay:}",
        cron = "\${trigger.cron:}"
    )])
annotation class PollableSource
