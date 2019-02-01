package org.carbon.crawler.stream.integration.case

import io.kotlintest.matchers.haveSize
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.carbon.crawler.model.domain.HostEntity
import org.carbon.crawler.model.domain.HostRepository
import org.carbon.crawler.model.domain.SnapShotRepository
import org.carbon.crawler.model.extend.exposed.beginTransaction
import org.carbon.crawler.model.infra.record.PageTable
import org.carbon.crawler.stream.core.config.DataSourceConfig
import org.carbon.crawler.stream.integration.extend.kompose.ServeHtml
import org.carbon.crawler.stream.integration.extend.matcher.shouldBeFound
import org.carbon.crawler.stream.integration.extend.spring.dataFlow.AbstractStreamTests
import org.carbon.crawler.stream.integration.extend.spring.dataFlow.StreamDefinition
import org.carbon.kompose.kompose
import org.jetbrains.exposed.sql.selectAll
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("dev")
@SpringBootTest(classes = [DataSourceConfig::class])
class ListingSinkStreamTest : AbstractStreamTests() {

    @Suppress("FunctionName")
    @Test
    internal fun register_create_deploy_stream(): Unit = kompose(ServeHtml(40101)) {
        // ______________________________________________________
        //
        // @ Given
        val host = "http://host.docker.internal:40101"
        val entity = HostEntity(
            null,
            host,
            "carbon wiki",
            "this is test host",
            emptyList(),
            HostEntity.Query(
                null,
                "list.html",
                "xpath:///html/body/div/ul/li/a",
                listOf(
                    HostEntity.DetailQuery(
                        null,
                        "version",
                        "xpath:///html/body/section[1]/pre",
                        "text/text"
                    ),
                    HostEntity.DetailQuery(
                        null,
                        "feature",
                        "xpath:///html/body/section[2]/pre",
                        "text/text"
                    ),
                    HostEntity.DetailQuery(
                        null,
                        "dependency",
                        "xpath:///html/body/section[3]/pre",
                        "text/text"
                    )
                )
            ))

        beginTransaction {
            HostRepository.save(entity)
        }

        // ______________________________________________________
        //
        // @ When
        val stream = StreamDefinition(
            "crawl-stream",
            "crawl-listing-source | crawl-listing-sink",
            mapOf(
                "app.crawl-listing-source.trigger.fixedDelay" to "1000",
                "app.crawl-listing-source.trigger.initialDelay" to "0",
                "app.crawl-listing-source.trigger.maxMessage" to "10"
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
            find = beginTransaction { PageTable.selectAll().count() > 0 }
            LoggerFactory.getLogger(ListingSinkStreamTest::class.java).info { "wait streaming is started" }
            if (cnt++ > maxRetry)
                throw IllegalStateException("data is not updated, maxRetry: $maxRetry")
            Thread.sleep(sleep)
        }

        val entities = beginTransaction {
            SnapShotRepository.fetch(0, 3)
        }

        entities should haveSize(3)

        val authEntry = entities.find { it.title == "Carbon | Authentication" }
        authEntry shouldNotBe null
        authEntry!!.title shouldBe "Carbon | Authentication"
        authEntry.url shouldBe "$host/modules/auth.html"
        authEntry.attributes.shouldBeFound({ it.key == "version" }) {
            it.value shouldBe "0.1.0-BETA"
        }
        authEntry.attributes.shouldBeFound({ it.key == "feature" }) {
            it.value shouldBe "Open-ended Authentication"
        }
        authEntry.attributes.shouldBeFound({ it.key == "dependency" }) {
            it.value shouldBe "carbon-component,carbon-util,carbon-modular,carbon-web"
        }

        val persistEntry = entities.find { it.title == "Carbon | Persistent" }
        persistEntry shouldNotBe null
        persistEntry!!.title shouldBe "Carbon | Persistent"
        persistEntry.url shouldBe "$host/modules/persistent.html"
        persistEntry.attributes.shouldBeFound({ it.key == "version" }) {
            it.value shouldBe "0.1.0-BETA"
        }
        persistEntry.attributes.shouldBeFound({ it.key == "feature" }) {
            it.value shouldBe "Persistent Facade,SQL Dialect Resolver"
        }
        persistEntry.attributes.shouldBeFound({ it.key == "dependency" }) {
            it.value shouldBe "carbon-component,carbon-modular"
        }

        val webEntry = entities.find { it.title == "Carbon | Web" }
        webEntry shouldNotBe null
        webEntry!!.title shouldBe "Carbon | Web"
        webEntry.url shouldBe "$host/modules/web.html"
        webEntry.attributes.shouldBeFound({ it.key == "version" }) {
            it.value shouldBe "0.1.0-BETA"
        }
        webEntry.attributes.shouldBeFound({ it.key == "feature" }) {
            it.value shouldBe "Modularized & Pluggable Web Framework"
        }
        webEntry.attributes.shouldBeFound({ it.key == "dependency" }) {
            it.value shouldBe "carbon-component,carbon-util,carbon-modular"
        }
    }
}