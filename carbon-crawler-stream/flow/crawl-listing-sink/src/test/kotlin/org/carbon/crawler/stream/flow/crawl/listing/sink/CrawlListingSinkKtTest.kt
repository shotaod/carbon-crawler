package org.carbon.crawler.stream.flow.crawl.listing.sink

import org.carbon.crawler.model.domain.base.Expeditions
import org.carbon.crawler.model.domain.base.Hosts
import org.carbon.crawler.model.domain.repo.ExpeditionRepository
import org.carbon.crawler.model.domain.repo.HostRepository
import org.carbon.crawler.model.extend.kompose.DBUtil
import org.carbon.crawler.model.extend.kompose.RollbackTransaction
import org.carbon.crawler.stream.core.config.DataSourceConfig
import org.carbon.crawler.stream.core.config.WebDriverConfig
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.carbon.crawler.stream.message.ExpeditionPayload
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
        // ______________________________________________________
        //
        // @ Given
        with(context[DBUtil::class]) {
            clean()
        }


        val exHostEtt = Hosts.new(
            "https://www.iana.org",
            "www.iana.org"
        )
        val exExpEtt = Expeditions.new(
            exHostEtt.id,
            { nextId ->
                CrawlRouting(
                    nextId(),
                    "https://www.iana.org",
                    "domains/reserved",
                    "xpath:////*[@id=\"arpa-table\"]/tbody/tr/td/span/a"
                )
            },
            { nextId ->
                listOf(
                    ScrapingPolicy(
                        nextId(),
                        "about",
                        "xpath:////*[@id=\"main_right\"]",
                        "text/text"
                    ))
            })

        HostRepository.save(exHostEtt)
        ExpeditionRepository.save(exExpEtt)
        crawlListingSink(driverFactory)(ExpeditionPayload(exExpEtt.id.value))
    }
}