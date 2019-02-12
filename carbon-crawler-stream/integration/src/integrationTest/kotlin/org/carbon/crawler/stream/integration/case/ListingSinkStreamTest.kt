package org.carbon.crawler.stream.integration.case

import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.carbon.crawler.model.domain.base.Expeditions
import org.carbon.crawler.model.domain.base.Hosts
import org.carbon.crawler.model.domain.repo.ExpeditionRepository
import org.carbon.crawler.model.domain.repo.HostRepository
import org.carbon.crawler.model.domain.repo.SnapshotRepository
import org.carbon.crawler.model.domain.shared.all
import org.carbon.crawler.model.extend.exposed.beginTransaction
import org.carbon.crawler.model.infra.record.SnapshotTable
import org.carbon.crawler.stream.core.config.DataSourceConfig
import org.carbon.crawler.stream.integration.extend.kompose.ServeHtml
import org.carbon.crawler.stream.integration.extend.matcher.shouldBeFound
import org.carbon.crawler.stream.integration.extend.spring.dataflow.AbstractStreamTests
import org.carbon.crawler.stream.integration.extend.spring.dataflow.StreamDefinition
import org.carbon.kompose.kompose
import org.jetbrains.exposed.sql.selectAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("dev")
@SpringBootTest(classes = [DataSourceConfig::class])
class ListingSinkStreamTest : AbstractStreamTests() {

    val logger = LoggerFactory.getLogger(ListingSinkStreamTest::class.java)!!

    @Suppress("FunctionName")
    @Test
    internal fun register_create_deploy_stream(): Unit = kompose(ServeHtml(40101)) {
        // ______________________________________________________
        //
        // @ Given
        val hostUrl = "http://host.docker.internal:40101"
        val hostEtt = Hosts.new(
            "http://host.docker.internal:40101",
            "carbon wiki",
            "this is test host"
        )

        val expedition = Expeditions.new(
            hostEtt.id,
            { nextId ->
                CrawlRouting(
                    nextId(),
                    hostUrl,
                    "list.html",
                    "xpath:///html/body/div/ul/li/a"
                )
            },
            { nextId ->
                listOf(
                    ScrapingPolicy(
                        nextId(),
                        "version",
                        "xpath:///html/body/section[1]/pre",
                        "text/text"
                    ),
                    ScrapingPolicy(
                        nextId(),
                        "feature",
                        "xpath:///html/body/section[2]/pre",
                        "text/text"
                    ),
                    ScrapingPolicy(
                        nextId(),
                        "dependency",
                        "xpath:///html/body/section[3]/pre",
                        "text/text"
                    )
                )
            }
        )

        beginTransaction {
            HostRepository.save(hostEtt)
            ExpeditionRepository.save(expedition)
        }

        // ______________________________________________________
        //
        // @ When
        val stream = StreamDefinition(
            "crawl-stream",
            "expedition-iterator-source | crawl-listing-sink",
            mapOf(
                "app.expedition-iterator-source.trigger.fixedDelay" to "10000",
                "app.expedition-iterator-source.trigger.maxMessage" to "10"
            ))
        deployStream(stream)

        // ______________________________________________________
        //
        // @ Then
        var cnt = 0
        val maxRetry = 100
        val sleep = 1000L
        var find = false
        while (!find) {
            find = beginTransaction { SnapshotTable.selectAll().count() > 0 }
            logger.info("wait streaming is started")
            if (cnt++ > maxRetry)
                throw IllegalStateException("data is not updated, maxRetry: $maxRetry")
            Thread.sleep(sleep)
        }

        val entities = beginTransaction {
            SnapshotRepository.fetchPage(all(0, 3)).items
        }

        entities should haveSize(3)

        val authEntry = entities.find { it.title == "Carbon | Authentication" }
        authEntry shouldNotBe null
        authEntry!!.title shouldBe "Carbon | Authentication"
        authEntry.url shouldBe "$hostUrl/modules/auth.html"
        authEntry.snapshotAttribute.shouldBeFound({ it.key == "version" }) {
            it.value shouldBe "0.1.0-BETA"
        }
        authEntry.snapshotAttribute.shouldBeFound({ it.key == "feature" }) {
            it.value shouldBe "Open-ended Authentication"
        }
        authEntry.snapshotAttribute.shouldBeFound({ it.key == "dependency" }) {
            it.value shouldBe "carbon-component,carbon-util,carbon-modular,carbon-web"
        }

        val persistEntry = entities.find { it.title == "Carbon | Persistent" }
        persistEntry shouldNotBe null
        persistEntry!!.title shouldBe "Carbon | Persistent"
        persistEntry.url shouldBe "$hostUrl/modules/persistent.html"
        persistEntry.snapshotAttribute.shouldBeFound({ it.key == "version" }) {
            it.value shouldBe "0.1.0-BETA"
        }
        persistEntry.snapshotAttribute.shouldBeFound({ it.key == "feature" }) {
            it.value shouldBe "Persistent Facade,SQL Dialect Resolver"
        }
        persistEntry.snapshotAttribute.shouldBeFound({ it.key == "dependency" }) {
            it.value shouldBe "carbon-component,carbon-modular"
        }

        val webEntry = entities.find { it.title == "Carbon | Web" }
        webEntry shouldNotBe null
        webEntry!!.title shouldBe "Carbon | Web"
        webEntry.url shouldBe "$hostUrl/modules/web.html"
        webEntry.snapshotAttribute.shouldBeFound({ it.key == "version" }) {
            it.value shouldBe "0.1.0-BETA"
        }
        webEntry.snapshotAttribute.shouldBeFound({ it.key == "feature" }) {
            it.value shouldBe "Modularized & Pluggable Web Framework"
        }
        webEntry.snapshotAttribute.shouldBeFound({ it.key == "dependency" }) {
            it.value shouldBe "carbon-component,carbon-util,carbon-modular"
        }
    }
}