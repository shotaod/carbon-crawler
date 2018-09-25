package org.carbon.crawler.step.crawle

import org.carbon.crawler.batch.config.DataConfig
import org.carbon.crawler.batch.config.PropertyConfig
import org.carbon.crawler.batch.config.WebDriverConfig
import org.carbon.crawler.batch.step.crawle.DictionaryReader
import org.carbon.crawler.model.DictionaryEntity
import org.carbon.crawler.model.DictionaryTable
import org.carbon.crawler.model.extend.exposed.transactionL
import org.carbon.crawler.test.lib.batch.SpringBatchTestConfig
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Soda 2018/07/28.
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
class DictionaryReaderTest {
    @Autowired
    lateinit var reader: DictionaryReader

    @Test
    fun read() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        transactionL {
            create(DictionaryTable)
            DictionaryEntity.new {
                this.title = "title-1"
                this.url = "https://www.example.com/title-1"
                this.memo = "this is test"
            }
            DictionaryEntity.new {
                this.title = "title-2"
                this.url = "https://www.example.com/title-2"
                this.memo = "this is test"
            }
            DictionaryEntity.new {
                this.title = "title-3"
                this.url = "https://www.example.com/title-3"
                this.memo = "this is test"
            }
        }
        reader.size = 2
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        val result1 = transactionL { reader.read() }
        val result2 = transactionL { reader.read() }
        val result3 = transactionL { reader.read() }
        val result4 = transactionL { reader.read() }
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        assertThat("first call should return value", result1, notNullValue())
        assertThat("first call value is correct", result1?.url, equalTo("https://www.example.com/title-1"))
        assertThat("second call should return value", result2, notNullValue())
        assertThat("second call value is correct", result2?.url, equalTo("https://www.example.com/title-2"))
        assertThat("third call should return value", result3, notNullValue())
        assertThat("third call value is correct", result3?.url, equalTo("https://www.example.com/title-3"))
        assertThat("forth call should return null", result4, nullValue())
    }
}
