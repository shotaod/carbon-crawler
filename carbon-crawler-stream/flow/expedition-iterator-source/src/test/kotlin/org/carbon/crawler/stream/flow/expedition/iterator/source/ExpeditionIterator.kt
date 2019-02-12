package org.carbon.crawler.stream.flow.expedition.iterator.source

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.carbon.crawler.model.domain.base.Expeditions
import org.carbon.crawler.model.domain.base.Hosts
import org.carbon.crawler.model.domain.repo.ExpeditionRepository
import org.carbon.crawler.model.domain.repo.HostRepository
import org.carbon.crawler.model.domain.shared.all
import org.carbon.crawler.model.extend.kompose.DBUtil
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
internal class ExpeditionIteratorKtTest {

    @Test
    fun test_iterator(): Unit = kompose(RollbackTransaction) {
        // ______________________________________________________
        //
        // @ Given
        with(context[DBUtil::class]) {
            clean()
        }
        val exHostEtt = Hosts.new(
            "example.com",
            "example.com"
        )
        val wwwExHostEtt = Hosts.new(
            "www.example.com",
            "www.example.com"
        )
        val exExpEtt = Expeditions.new(
            exHostEtt.id,
            { nextId ->
                CrawlRouting(
                    nextId(),
                    "example.com",
                    "/list",
                    "xpath://foo/bar/baz"
                )
            },
            { nextId ->
                listOf(
                    ScrapingPolicy(
                        nextId(),
                        "key",
                        "xpath://key",
                        "text/text"
                    ),
                    ScrapingPolicy(
                        nextId(),
                        "title",
                        "xpath://key",
                        "text/text"
                    ))
            })
        val wwwExExpEtt = Expeditions.new(
            wwwExHostEtt.id,
            { nextId ->
                CrawlRouting(
                    nextId(),
                    "www.example.com",
                    "/list",
                    "xpath://foo/bar/baz"
                )
            },
            { nextId ->
                listOf(
                    ScrapingPolicy(
                        nextId(),
                        "key",
                        "xpath://key",
                        "text/text"
                    ),
                    ScrapingPolicy(
                        nextId(),
                        "title",
                        "xpath://title",
                        "text/text"
                    ),
                    ScrapingPolicy(
                        nextId(),
                        "date",
                        "xpath://date",
                        "text/text"
                    )
                )
            }
        )
        HostRepository.bulkSave(listOf(exHostEtt, wwwExHostEtt))
        ExpeditionRepository.bulkSave(listOf(exExpEtt, wwwExExpEtt))

        val res = ExpeditionRepository.fetchPage(all(0, 100))
        println(res.items)

        // ______________________________________________________
        //
        // @ When & Then
        // 1st
        with(expeditionIterator()) {
            this shouldNotBe null
        }
        // 2nd
        with(expeditionIterator()) {
            this shouldNotBe null
        }
        // 3rd
        with(expeditionIterator()) {
            this shouldBe null
        }

    }
}