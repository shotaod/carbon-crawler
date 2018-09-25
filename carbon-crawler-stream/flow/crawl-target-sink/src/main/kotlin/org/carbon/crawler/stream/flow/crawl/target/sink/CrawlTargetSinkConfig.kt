package org.carbon.crawler.stream.flow.crawl.target.sink

import org.carbon.crawler.stream.core.config.DataConfig
import org.carbon.crawler.stream.core.config.WebDriverConfig
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.carbon.crawler.stream.core.extend.spring.cloud.SinkConfigSupport
import org.carbon.crawler.stream.message.crawlTarget.CrawlTargetPayload
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
class CrawlTargetSinkConfig(
        driverFactory: DriverFactory,
        channel: Sink
) : SinkConfigSupport<CrawlTargetPayload>(
        CrawlTargetPayload::class,
        channel,
        createSink(driverFactory)
)