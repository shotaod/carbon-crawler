package org.carbon.crawler.step.crawle

import org.carbon.crawler.batch.config.DataConfig
import org.carbon.crawler.batch.config.PropertyConfig
import org.carbon.crawler.batch.config.WebDriverConfig
import org.carbon.crawler.batch.step.crawle.CrawlScrapingProcessor
import org.carbon.crawler.batch.step.crawle.item.OrderItem
import org.carbon.crawler.batch.step.crawle.item.PageItem
import org.carbon.crawler.test.lib.batch.SpringBatchTestConfig
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.openqa.selenium.By
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
class CrawlScrapingProcessorTest {
    @Autowired
    lateinit var processor: CrawlScrapingProcessor

    @Before
    fun setUp() {
    }

    @Test
    fun process() {
        val item = OrderItem(
                dictionaryId = 1,
                url = "https://www.min-breeder.com/",
                range = IntRange(10, 20),
                directList = { findElementById("globalNavi").findElement(By.tagName("a")).click() },
                fetchDetailPagePaths = { findElementsByClassName("pic_kind_name").map { it.findElement(By.tagName("a")).getAttribute("href") } },
                fetchDetailPageAttributes = {
                    listOf(
                            "breed" to findElementByClassName("petDtlHdrName").text,
                            "price" to findElementByClassName("gnrTblPrc").text,
                            "location" to findElementByXPath("//*[@id=\"main\"]/div/div[2]/table/tbody/tr[2]/td[1]").text,
                            "img" to findElementById("detail_img").getAttribute("style"))
                            .fold(HashMap(), operation = { map, item ->
                                map[item.first] = item.second
                                map
                            })
                }
        )
        val result = processor.process(item)
        val detailItem: PageItem = result.items[0]

        assertThat("keep id", result.dictionaryId, equalTo<Long>(1))
        assertTrue("success fetch items", result.items.isNotEmpty())
        assertThat("item1 is toy poodle", detailItem.attributes["breed"], instanceOf(String::class.java))
        assertThat("item1 is toy poodle", detailItem.attributes["breed"].toString(), containsString("トイプードル"))
    }
}