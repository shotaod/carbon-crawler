package org.carbon.crawler.stream.core.extend.spring.cloud

import org.springframework.cloud.stream.messaging.Sink
import org.springframework.context.annotation.Bean
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.json.JsonToObjectTransformer
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import kotlin.reflect.KClass

typealias TypedSink<PAYLOAD> = (PAYLOAD) -> Unit

/**
 * Configuration Support for Spring Cloud [MessageHandler]
 * with typed message handling
 * ``````
 * sample usage
 * ``````
 * ```kotlin
 * @EnableBinding(Sink::class)
 * @Configuration
 * class SinkStreamConfig(val someDependency: SomeDependency)
 *   : SinkConfigSupport<Input> {
 *   override val messageSink: TypedSink<Input> = {  handle it }
 * }
 * ```
 *
 * @author Soda 2018/08/10.
 */
abstract class SinkConfigSupport<T : Any>(
        private val payloadClass: KClass<T>,
        private val channel: Sink,
        private val sink: TypedSink<T>
) {
    protected interface MessageSink<BODY> : MessageHandler {
        private inline fun <reified T> Any.alsoTyped(also: (T) -> Unit) {
            if (this is T) also(this)
        }

        override fun handleMessage(message: Message<*>) {
            message.alsoTyped<Message<BODY>> {
                handle(it.payload)
            }
        }

        val handle: TypedSink<BODY>
    }

    protected fun TypedSink<T>.toHandler() = object : MessageSink<T> {
        override val handle: TypedSink<T> = this@toHandler
    }

    @Bean
    open fun sinkFlow(): IntegrationFlow {
        return IntegrationFlows
                .from(channel.input())
                .transform(JsonToObjectTransformer(payloadClass.java))
                .handle(sink.toHandler())
                .get()
    }
}
