package org.carbon.crawler.step.crawle

import fi.iki.elonen.NanoHTTPD
import org.carbon.crawler.batch.config.DataConfig
import org.carbon.crawler.batch.config.PropertyConfig
import org.carbon.crawler.batch.config.WebDriverConfig
import org.carbon.crawler.batch.extend.selenium.DriverFactory
import org.carbon.crawler.batch.step.crawle.CrawlOrderProcessor
import org.carbon.crawler.batch.step.crawle.item.DictionaryItem
import org.carbon.crawler.batch.step.crawle.item.OrderItem
import org.carbon.crawler.model.CrawlQueryEntity
import org.carbon.crawler.model.CrawlQueryTable
import org.carbon.crawler.model.CrawlRootDocEntity
import org.carbon.crawler.model.CrawlRootDocTable
import org.carbon.crawler.model.DictionaryEntity
import org.carbon.crawler.model.DictionaryTable
import org.carbon.crawler.model.extend.exposed.transactionL
import org.carbon.crawler.test.lib.batch.SpringBatchTestConfig
import org.carbon.crawler.test.lib.httpd.FsHtmlServer
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Soda 2018/07/29.
 */
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(
        classes = [
            DataConfig::class,
            PropertyConfig::class,
            WebDriverConfig::class,
            SpringBatchTestConfig::class],
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CrawlOrderProcessorTest {
    infix fun <A, B, C> Pair<A, B>.to(c: C): Triple<A, B, C> = Triple(first, second, c)

    @Autowired
    lateinit var processor: CrawlOrderProcessor
    @Autowired
    lateinit var driverFactory: DriverFactory
    lateinit var server: FsHtmlServer

    @Before
    fun setUp() {
        server = FsHtmlServer(8181, "/html")
        server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
    }

    @Test
    fun process() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        transactionL {
            addLogger(StdOutSqlLogger)
            create(DictionaryTable)
            create(CrawlRootDocTable)
            create(CrawlQueryTable)
        }
        val url = "http://host.docker.internal:8181/top.html"
        val dictionary = transactionL {
            val dictionary = DictionaryEntity.new {
                this.url = url
                this.title = "carbon.org"
            }
            val scrapingCommonDirectionEntity = CrawlRootDocEntity.new {
                this.dictionary = dictionary
                this.listPagePath = "list.html"
                this.listHolderQuery = "xpath:/html/body/div/ul"
                this.listItemQuery = "xpath:li/a"
            }

            CrawlQueryEntity.new {
                this.crawlRootDocEntity = scrapingCommonDirectionEntity
                this.name = "module"
                this.query = "xpath:/html/body/table/tbody/tr[1]/td"
            }

            CrawlQueryEntity.new {
                this.crawlRootDocEntity = scrapingCommonDirectionEntity
                this.name = "function"
                this.query = "xpath:/html/body/table/tbody/tr[2]/td"
            }

            dictionary
        }
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        val item: OrderItem = transactionL {
            processor.process(DictionaryItem(dictionary.id.value, dictionary.url))
        }

        val (_, paths, attributes) = driverFactory.setupDriver(ChromeOptions()).use { driver ->
            driver.get(url)
            item.directList(driver)
            val detailPagePaths = item.fetchDetailPagePaths(driver)
            val attributes = detailPagePaths
                    .flatMap {
                        driver.get(it)
                        item.fetchDetailPageAttributes(driver)
                                .entries
                                .map { it.toPair() }
                    }

            item.dictionaryId to detailPagePaths to attributes
        }
        // -----------------------------------------------------
        //                                               assert
        //                                               -------
        assertThat("check dictionaryId", item.dictionaryId, equalTo(1L))
        assertThat("check dictionaryId", item.url, equalTo(url))

        assertThat("detail page is correct", paths[0], endsWith("/detail_0.html"))
        assertThat("detail page is correct", paths[1], endsWith("/detail_1.html"))
        assertThat("detail page is correct", paths[2], endsWith("/detail_2.html"))

        assertThat("attribute count is correct", attributes.size, equalTo(2 * 3))
        assertThat("attribute value is correct", attributes[0], equalTo(Pair("module", "carbon-component, carbon-modular, carbon-util")))
        assertThat("attribute value is correct", attributes[1], equalTo(Pair("function", "DI, modularization, java config")))
        assertThat("attribute value is correct", attributes[2], equalTo(Pair("module", "carbon-authentication")))
        assertThat("attribute value is correct", attributes[3], equalTo(Pair("function", "open ended authentication")))
        assertThat("attribute value is correct", attributes[4], equalTo(Pair("module", "carbon-persistent")))
        assertThat("attribute value is correct", attributes[5], equalTo(Pair("function", "pluggable connector for ORM")))
    }
}
