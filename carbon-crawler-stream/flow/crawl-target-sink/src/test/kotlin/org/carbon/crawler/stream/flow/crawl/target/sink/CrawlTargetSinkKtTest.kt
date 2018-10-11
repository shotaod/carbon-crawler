package org.carbon.crawler.stream.flow.crawl.target.sink

import fi.iki.elonen.NanoHTTPD
import org.carbon.crawler.model.CrawlQueryTable
import org.carbon.crawler.model.CrawlRootDocEntity
import org.carbon.crawler.model.CrawlRootDocTable
import org.carbon.crawler.model.CrawlSourceTable
import org.carbon.crawler.model.DictionaryEntity
import org.carbon.crawler.model.extend.exposed.transactionL
import org.carbon.crawler.stream.core.config.DataConfig
import org.carbon.crawler.stream.core.config.PropertyConfig
import org.carbon.crawler.stream.core.config.WebDriverConfig
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.carbon.crawler.stream.message.crawlTarget.CrawlTargetPayload
import org.carbon.crawler.stream.test.FsHtmlServer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Soda 2018/08/22.
 */
@Ignore
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(
        classes = [
            DataConfig::class,
            WebDriverConfig::class,
            PropertyConfig::class],
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CrawlTargetSinkKtTest {
    val execute: NanoHTTPD.() -> Unit = { this.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false) }
    @Autowired
    lateinit var driverFactory: DriverFactory

    @Before
    fun setUp() {
        transactionL {
            SchemaUtils.create(CrawlRootDocTable)
            SchemaUtils.create(CrawlSourceTable)
            SchemaUtils.create(CrawlQueryTable)
        }
    }

    @Test
    fun test_list_has_single_item() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        FsHtmlServer(50080, "/pages/carbon_core").execute()

        transactionL {
            val dictionary = DictionaryEntity.new {
                url = "http://host.docker.internal:50080/top.html"
                title = "carbon_core"
                memo = "memo"
            }
            CrawlRootDocEntity.new(1L) {
                this.dictionary = dictionary
                listPagePath = "http://host.docker.internal:50080/list.html"
                listHolderQuery = "xpath:/html/body/div/ul"
                listItemQuery = "xpath:li/a"
            }
        }
        val payload = CrawlTargetPayload(1L)
        val sink = createSink(driverFactory)
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        sink(payload)
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        val rootDoc = transactionL {
            CrawlRootDocEntity.findById(1L) ?: throw IllegalStateException("not saved root doc")
        }

        assertThat("crawlOrderSource is bind to rootDoc correctly", rootDoc.sources.size, equalTo(1))
        assertThat("list url is correct", rootDoc.sources.first().url, equalTo("http://host.docker.internal:50080/modules/detail.html"))
    }

    @Test
    fun test_multiple_list_page() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        FsHtmlServer(50080, "/pages/carbon_wiki").execute()
        transactionL {
            val dictionary = DictionaryEntity.new {
                url = "http://host.docker.internal:50080/top.html"
                title = "carbon_core"
                memo = "memo"
            }
            CrawlRootDocEntity.new(1L) {
                this.dictionary = dictionary
                listPagePath = "http://host.docker.internal:50080/list.html"
                listHolderQuery = "xpath:/html/body/div/ul"
                listItemQuery = "xpath:li/a"
            }
        }
        val payload = CrawlTargetPayload(1L)
        val sink = createSink(driverFactory)
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        sink(payload)
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        val rootDoc = transactionL {
            CrawlRootDocEntity.findById(1L) ?: throw IllegalStateException("not saved root doc")
        }

        assertThat("crawlOrderSource is bind to rootDoc correctly", rootDoc.sources.size, equalTo(3))
        assertThat("list url should be detail_0", rootDoc.sources[0].url, equalTo("http://host.docker.internal:50080/detail_0.html"))
        assertThat("list url should be detail_1", rootDoc.sources[1].url, equalTo("http://host.docker.internal:50080/detail_1.html"))
        assertThat("list url should be detail_2", rootDoc.sources[2].url, equalTo("http://host.docker.internal:50080/detail_2.html"))
    }
}