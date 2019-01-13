package org.carbon.crawler.stream.flow.crawl.listing.source

import org.carbon.crawler.model.domain.HostRepository
import org.carbon.crawler.stream.message.crawlOrder.ListingOrderPayload
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.atomic.AtomicInteger

private val currentIndex: AtomicInteger = AtomicInteger(0)

fun crawlOrderSource(): ListingOrderPayload? = transaction {
    HostRepository.fetch(currentIndex.getAndIncrement(), 1)
        .singleOrNull()
        ?.let {
            ListingOrderPayload(it.id!!)
        }
        ?: {
            currentIndex.set(0)
            null
        }()
}
