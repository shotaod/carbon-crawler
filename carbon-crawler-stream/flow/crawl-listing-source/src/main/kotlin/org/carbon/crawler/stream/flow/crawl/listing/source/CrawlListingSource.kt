package org.carbon.crawler.stream.flow.crawl.listing.source

import org.carbon.crawler.model.domain.HostRepository
import org.carbon.crawler.stream.message.TargetHostPayload
import java.util.concurrent.atomic.AtomicInteger

private val currentIndex: AtomicInteger = AtomicInteger(0)

fun crawlListingSource(): TargetHostPayload? =
    HostRepository.fetch(currentIndex.getAndIncrement(), 1)
        .singleOrNull()
        ?.let { TargetHostPayload(it.id!!) }
        ?: null.also { currentIndex.set(0) }
