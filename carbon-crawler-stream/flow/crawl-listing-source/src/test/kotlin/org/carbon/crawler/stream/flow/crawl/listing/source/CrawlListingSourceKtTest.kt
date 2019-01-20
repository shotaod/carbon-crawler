package org.carbon.crawler.stream.flow.crawl.listing.source

import org.carbon.crawler.stream.core.config.DataConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("dev")
@EnableConfigurationProperties
@SpringBootTest(classes = [DataConfig::class])
internal class CrawlListingSourceKtTest {

    @Test
    fun test_crawlListingSource() {
        crawlListingSource()
    }
}