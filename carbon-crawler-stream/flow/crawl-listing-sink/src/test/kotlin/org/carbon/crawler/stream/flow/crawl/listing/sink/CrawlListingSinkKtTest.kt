package org.carbon.crawler.stream.flow.crawl.listing.sink

import org.carbon.crawler.model.extend.kompose.RollbackTransaction
import org.carbon.crawler.stream.core.config.DataSourceConfig
import org.carbon.crawler.stream.core.config.WebDriverConfig
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.carbon.crawler.stream.message.TargetHostPayload
import org.carbon.kompose.kompose
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("dev")
@SpringBootTest(classes = [DataSourceConfig::class, WebDriverConfig::class])
class CrawlListingSinkKtTest {

    @Autowired
    lateinit var driverFactory: DriverFactory

    @Test
    fun test() = kompose(RollbackTransaction) {
        crawlListingSink(driverFactory)(TargetHostPayload(1L))
    }
}