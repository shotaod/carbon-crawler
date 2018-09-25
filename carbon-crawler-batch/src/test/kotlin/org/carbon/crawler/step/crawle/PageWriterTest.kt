package org.carbon.crawler.step.crawle

import org.carbon.crawler.batch.config.DataConfig
import org.carbon.crawler.batch.config.PropertyConfig
import org.carbon.crawler.batch.config.WebDriverConfig
import org.carbon.crawler.batch.step.crawle.PageWriter
import org.carbon.crawler.batch.step.crawle.item.PageChunkItem
import org.carbon.crawler.batch.step.crawle.item.PageItem
import org.carbon.crawler.model.DictionaryEntity
import org.carbon.crawler.model.DictionaryTable
import org.carbon.crawler.model.PageAttributeEntity
import org.carbon.crawler.model.PageAttributeTable
import org.carbon.crawler.model.PageEntity
import org.carbon.crawler.model.PageTable
import org.carbon.crawler.model.extend.exposed.transactionL
import org.carbon.crawler.test.lib.batch.SpringBatchTestConfig
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
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
class PageWriterTest {
    @Autowired
    lateinit var writer: PageWriter

    @Test
    fun write_save_record() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        transactionL {
            create(DictionaryTable)
            create(PageTable)
            create(PageAttributeTable)
            DictionaryEntity.new(1) {
                title = "example.com"
                url = "https://example.com"
            }
        }
        val exampleAttributes = mapOf(
                "title" to "this is detail 1 page",
                "color" to "yellow"
        )
        val chunk = PageChunkItem(1).assign(PageItem(
                url = "https://example.com/detail/1",
                title = "example.com | detail 1",
                attributes = exampleAttributes
        ))
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        transactionL {
            writer.write(listOf(chunk))
        }

        val (pages, attributes) = transactionL {
            val pages = PageEntity.all().toList()
            val attributes = PageAttributeEntity.find { PageAttributeTable.pageId.eq(1L) }.sortedBy { it.id }
            pages to attributes
        }
        // -----------------------------------------------------
        //                                               assert
        //                                               -------
        assertThat("new page record is saved?", pages.count(), equalTo(1))
        assertThat("new page attributes are saved?", attributes.count(), equalTo(2))
        assertThat("attribute title key", attributes[0].key, equalTo("title"))
        assertThat("attribute title value", attributes[0].value, equalTo("this is detail 1 page"))
        assertThat("attribute color key", attributes[1].key, equalTo("color"))
        assertThat("attribute color value", attributes[1].value, equalTo("yellow"))
    }
}