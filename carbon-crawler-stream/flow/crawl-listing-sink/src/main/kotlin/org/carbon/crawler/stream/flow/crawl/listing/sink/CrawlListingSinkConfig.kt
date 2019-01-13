package org.carbon.crawler.stream.flow.crawl.listing.sink

import org.carbon.composer.compose
import org.carbon.crawler.model.extend.composer.Transaction
import org.carbon.crawler.stream.core.config.DataConfig
import org.carbon.crawler.stream.core.config.WebDriverConfig
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.carbon.crawler.stream.core.extend.spring.cloud.SinkConfigSupport
import org.carbon.crawler.stream.message.crawlOrder.ListingOrderPayload
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author Soda 2018/08/13.
 */
@Import(DataConfig::class, WebDriverConfig::class)
@EnableBinding(Sink::class)
@Configuration
class CrawlListingSinkConfig(
    sink: Sink,
    driverFactory: DriverFactory
) : SinkConfigSupport<ListingOrderPayload>(
    ListingOrderPayload::class,
    sink,
    compose(Transaction()) { crawlListingSink(driverFactory) }
)