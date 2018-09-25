package org.carbon.crawler.stream.core.extend.spring.cloud

import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.integration.core.MessageSource
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.endpoint.AbstractMessageSource
import org.springframework.integration.scheduling.PollerMetadata
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

typealias TypedSource<PAYLOAD> = () -> PAYLOAD?

/**
 * Configuration Support for Spring Cloud [MessageSource]
 * with configuring default poller
 * ``````
 * sample usage
 * ``````
 * ```kotlin
 * @EnableBinding(Source::class)
 * @Configuration
 * class SourceStreamConfig(poller: PollerMetadata)
 *   : SourceConfigSupport<CrawlOrder>(poller) {
 *   override val messageSource: TypedSource<Some> = { Some() }
 * }
 * ```
 *
 * @param OUT type of message body
 *
 * @author Soda 2018/08/08.
 */
abstract class SourceConfigSupport<OUT>(
        private var poller: PollerMetadata,
        private val channel: Source,
        private val source: TypedSource<OUT>
) {
    companion object {
        val count = AtomicInteger(0)
    }

    val logger = LoggerFactory.getLogger(this.javaClass)!!

    fun <T> TypedSource<T>.toSource(): MessageSource<T> = object : AbstractMessageSource<T>() {
        override fun getComponentType(): String = "p-${this::class.simpleName}-${count.incrementAndGet()}"
        override fun doReceive(): T? {
            val result: T? = this@toSource()
            when (result) {
                null -> logger.info("[source] null")
                else -> logger.info("[source] $result")
            }

            return result
        }
    }

    @Bean
    open fun sourceFlow(): IntegrationFlow = IntegrationFlows
            .from(source.toSource(), Consumer { it.poller(poller) })
            .channel(channel.output())
            .get()
}
