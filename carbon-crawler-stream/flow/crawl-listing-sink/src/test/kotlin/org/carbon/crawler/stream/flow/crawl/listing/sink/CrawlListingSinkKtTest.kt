package org.carbon.crawler.stream.flow.crawl.listing.sink

import org.carbon.composer.compose
import org.carbon.crawler.model.extend.composer.RollbackTransaction
import org.carbon.crawler.stream.core.config.DataConfig
import org.carbon.crawler.stream.core.config.WebDriverConfig
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.carbon.crawler.stream.message.crawlOrder.ListingOrderPayload
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("dev")
@EnableConfigurationProperties
@SpringBootTest(classes = [DataConfig::class, WebDriverConfig::class])
class CrawlListingSinkKtTest {

    @Autowired
    lateinit var driverFactory: DriverFactory

    @Test
    fun test() = compose(RollbackTransaction) {
        crawlListingSink(driverFactory)(ListingOrderPayload(1L))
    }
}