package org.carbon.crawler.stream.flow.crawl.listing.source

import org.carbon.crawler.model.extend.kompose.Transaction
import org.carbon.crawler.stream.core.config.DataSourceConfig
import org.carbon.crawler.stream.core.extend.spring.cloud.PollableSource
import org.carbon.crawler.stream.message.TargetHostPayload
import org.carbon.kompose.Context
import org.carbon.kompose.kompose
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author Soda 2018/08/13.
 */
@Import(DataSourceConfig::class)
@EnableBinding(Source::class)
@Configuration
class CrawlListingSourceConfig {
    val function: Context.() -> TargetHostPayload? = { crawlListingSource() }
    @PollableSource
    fun source(): TargetHostPayload? = kompose(Transaction(logging = true), expression = function)
}