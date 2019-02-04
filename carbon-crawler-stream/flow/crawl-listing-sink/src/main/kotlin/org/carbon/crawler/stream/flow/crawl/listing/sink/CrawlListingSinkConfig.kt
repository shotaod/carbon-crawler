package org.carbon.crawler.stream.flow.crawl.listing.sink

import org.carbon.crawler.model.extend.kompose.Transaction
import org.carbon.crawler.stream.core.config.DataSourceConfig
import org.carbon.crawler.stream.core.config.WebDriverConfig
import org.carbon.crawler.stream.core.extend.carbon.ExceptionLogging
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.carbon.crawler.stream.message.TargetHostPayload
import org.carbon.kompose.Context
import org.carbon.kompose.kompose1
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author Soda 2018/08/13.
 */
@Import(DataSourceConfig::class, WebDriverConfig::class)
@EnableBinding(Sink::class)
@Configuration
class CrawlListingSinkConfig(driverFactory: DriverFactory) {

    val contextualized: Context.(TargetHostPayload) -> Unit = { crawlListingSink(driverFactory)(it) }

    @StreamListener(Sink.INPUT)
    fun sink(payload: TargetHostPayload): Unit = kompose1(
        Transaction(logging = true),
        ExceptionLogging(this::class),
        expression = contextualized)(payload)
}