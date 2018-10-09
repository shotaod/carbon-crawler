package org.carbon.crawler.stream.flow.crawl.order.source

import org.carbon.crawler.model.CrawlQueryTable
import org.carbon.crawler.model.CrawlRootDocEntity
import org.carbon.crawler.model.CrawlRootDocTable
import org.carbon.crawler.model.CrawlSourceEntity
import org.carbon.crawler.model.CrawlSourceTable
import org.carbon.crawler.model.DictionaryEntity
import org.carbon.crawler.model.DictionaryTable
import org.carbon.crawler.model.extend.exposed.transactionL
import org.carbon.crawler.stream.core.config.DataConfig
import org.carbon.crawler.stream.core.config.PropertyConfig
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Soda 2018/08/22.
 */
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(
        classes = [
            DataConfig::class,
            PropertyConfig::class],
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CrawlSourceSourceTest {
    @Before
    fun setUp() {
        transactionL {
            SchemaUtils.create(DictionaryTable)
            SchemaUtils.create(CrawlRootDocTable)
            SchemaUtils.create(CrawlQueryTable)
            SchemaUtils.create(CrawlSourceTable)
        }
    }

    @Test
    fun test_single() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        transactionL {
            setupRootDocWithSingleSourceData()
        }
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        val payload = crawlOrderSource()
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        assertThat("message payload has correct dictionaryId", payload?.dictionaryId, equalTo(1L))
        assertThat("message payload has correct page url", payload?.url, equalTo("http://localhost:58080/modules/carbon-core.html"))
    }

    @Test
    fun test_multiple() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        val rootDoc = transactionL {
            setupRootDocWithSingleSourceData()
        }
        transactionL {
            CrawlSourceEntity.new {
                this.rootDoc = rootDoc
                url = "http://localhost:58080/modules/carbon-authentication.html"
            }
        }
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        val payload1 = crawlOrderSource()
        val payload2 = crawlOrderSource()
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        assertThat("message payload has correct dictionaryId", payload1?.dictionaryId, equalTo(1L))
        assertThat("message payload has correct page url", payload1?.url, equalTo("http://localhost:58080/modules/carbon-core.html"))

        assertThat("message payload has correct dictionaryId", payload2?.dictionaryId, equalTo(1L))
        assertThat("message payload has correct page url", payload2?.url, equalTo("http://localhost:58080/modules/carbon-authentication.html"))
    }

    @Test
    fun test_data_noting() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        // nothing
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        val message = crawlOrderSource()
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        assertThat("if message crawlOrderSource is empty, then return null", message, CoreMatchers.nullValue())
    }

    @Test
    fun test_discrete_id() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        val rootDoc = transactionL {
            setupRootDocWithSingleSourceData()
        }
        transactionL {
            CrawlSourceEntity.new(48L) {
                this.rootDoc = rootDoc
                url = "http://localhost:58080/modules/carbon-authentication.html"
            }
        }
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        val payload1 = crawlOrderSource()
        val payload2 = crawlOrderSource()
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        assertThat("if data discrete, then Being able to handle", payload1?.url, equalTo("http://localhost:58080/modules/carbon-core.html"))
        assertThat("if data discrete, then Being able to handle", payload2?.url, equalTo("http://localhost:58080/modules/carbon-authentication.html"))
    }

    @Test
    fun test_iterate_again() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        val rootDoc = transactionL {
            setupRootDocWithSingleSourceData()
        }
        transactionL {
            CrawlSourceEntity.new {
                this.rootDoc = rootDoc
                this.url = "http://localhost:58080/modules/carbon-authentication.html"
            }
        }
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        val payload1 = crawlOrderSource()
        /*val payload2 = */ crawlOrderSource()
        val payload3 = crawlOrderSource()
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        assertThat("iterate again successfully", payload1?.url, equalTo(payload3?.url))
    }

    // ===================================================================================
    //                                                                          Private
    //                                                                          ==========
    private fun setupRootDocWithSingleSourceData(): CrawlRootDocEntity {
        val dictionary = DictionaryEntity.new {
            url = "localhost:58080"
            title = "carbon_wiki"
        }
        val rootDoc = CrawlRootDocEntity.new {
            this.dictionary = dictionary
            listPagePath = "/list.html"
            listHolderQuery = "xpath:/html/body/div/ul"
            listItemQuery = "xpath:li/a"
        }
        CrawlSourceEntity.new {
            this.rootDoc = rootDoc
            url = "http://localhost:58080/modules/carbon-core.html"
        }

        return rootDoc
    }
}