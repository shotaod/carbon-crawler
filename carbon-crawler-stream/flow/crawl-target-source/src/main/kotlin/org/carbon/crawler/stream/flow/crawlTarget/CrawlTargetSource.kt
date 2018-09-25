package org.carbon.crawler.stream.flow.crawlTarget

import org.carbon.crawler.model.CrawlRootDocTable
import org.carbon.crawler.model.extend.exposed.transactionL
import org.carbon.crawler.stream.message.crawlTarget.CrawlTargetPayload
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import java.util.concurrent.atomic.AtomicInteger

fun crawlTargetSource(): CrawlTargetPayload? = transactionL {
    CrawlRootDocTable.selectAll()
            .orderBy(CrawlRootDocTable.id to SortOrder.ASC)
            .limit(1, currentIndex.getAndIncrement())
            .singleOrNull()
            ?.let { it[CrawlRootDocTable.id] }
            ?.let { CrawlTargetPayload(it.value) }
            ?: {
                currentIndex.set(0)
                if (CrawlRootDocTable.selectAll().count() == 0) null
                else crawlTargetSource()
            }()
}

private val currentIndex: AtomicInteger = AtomicInteger(0)
