package org.carbon.crawler.stream.flow.crawl.target.sink

import org.carbon.crawler.model.CrawlRootDocEntity
import org.carbon.crawler.model.CrawlSourceTable
import org.carbon.crawler.model.extend.exposed.batchInsertOnDuplicateKeyUpdate
import org.carbon.crawler.model.extend.exposed.transactionL
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.carbon.crawler.stream.core.extend.selenium.toBy
import org.carbon.crawler.stream.message.crawlTarget.CrawlTargetPayload
import java.net.URI
import java.time.LocalDateTime

/**
 * @author Soda 2018/08/10.
 */
fun createSink(df: DriverFactory): (CrawlTargetPayload) -> Unit = { payload ->
    val rootDocId = payload.rootDocId

    transactionL { CrawlRootDocEntity.findById(rootDocId) }
            ?.let { rootDoc ->
                val urls = df.use { driver ->
                    driver.get(rootDoc.listPagePath)
                    driver
                            .findElement(rootDoc.listHolderQuery.toBy())
                            .findElements(rootDoc.listItemQuery.toBy())
                            .map { element ->
                                val href = element.getAttribute("href")
                                URI(driver.currentUrl).resolve(href).toString()
                            }
                }
                val columns = listOf(CrawlSourceTable.updatedAt)
                transactionL {
                    CrawlSourceTable.batchInsertOnDuplicateKeyUpdate(urls, onDuplicate = columns) { url ->
                        this[CrawlSourceTable.crawlRootDocId] = rootDoc.id
                        this[CrawlSourceTable.url] = url
                        this[CrawlSourceTable.updatedAt] = LocalDateTime.now()
                    }
                }
            }
}
