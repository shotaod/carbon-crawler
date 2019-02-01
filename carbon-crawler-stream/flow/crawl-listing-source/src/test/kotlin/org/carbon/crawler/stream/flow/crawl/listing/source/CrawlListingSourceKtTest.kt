package org.carbon.crawler.stream.flow.crawl.listing.source

import org.carbon.crawler.model.extend.kompose.RollbackTransaction
import org.carbon.crawler.stream.core.config.DataSourceConfig
import org.carbon.kompose.kompose
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("dev")
@EnableConfigurationProperties
@SpringBootTest(classes = [DataSourceConfig::class])
internal class CrawlListingSourceKtTest {

    @Test
    fun test_crawlListingSource(): Unit = kompose(RollbackTransaction) {
        crawlListingSource()
    }
}