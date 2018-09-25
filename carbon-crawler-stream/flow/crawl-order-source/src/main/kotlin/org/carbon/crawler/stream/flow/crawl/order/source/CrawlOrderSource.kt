package org.carbon.crawler.stream.flow.crawl.order.source

import org.carbon.crawler.model.CrawlSourceEntity
import org.carbon.crawler.model.CrawlSourceTable
import org.carbon.crawler.model.extend.exposed.transactionL
import org.carbon.crawler.stream.message.crawlOrder.CrawlOrderPayload
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import java.util.concurrent.atomic.AtomicInteger

fun crawlOrderSource(): CrawlOrderPayload? = transactionL {
    CrawlSourceTable.selectAll()
            .orderBy(CrawlSourceTable.id to SortOrder.ASC)
            .limit(1, currentIndex.getAndIncrement())
            .singleOrNull()
            ?.let { it[CrawlSourceTable.id] }
            ?.let { CrawlSourceEntity.findById(it) }
            ?.let { CrawlOrderPayload(it.rootDoc.dictionary.id.value, it.url) }
            ?: {
                currentIndex.set(0)
                if (CrawlSourceTable.selectAll().count() == 0) null
                else crawlOrderSource()
            }()
}

private val currentIndex: AtomicInteger = AtomicInteger(0)
