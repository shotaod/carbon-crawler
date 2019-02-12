package org.carbon.crawler.stream.flow.expedition.iterator.source

import org.carbon.crawler.model.domain.repo.ExpeditionRepository
import org.carbon.crawler.model.domain.shared.all
import org.carbon.crawler.stream.message.ExpeditionPayload
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

private val logger = LoggerFactory.getLogger("expedition.iterator.source")
private val currentIndex: AtomicInteger = AtomicInteger(0)

fun expeditionIterator(): ExpeditionPayload? {
    val page = ExpeditionRepository.fetchPage(all(currentIndex.getAndIncrement(), 1))
    return page.items.singleOrNull()
        ?.let {
            logger.info("found: offset {},  expeditionID: {}", currentIndex.get(), it.id.value)
            ExpeditionPayload(it.id.value)
        }
        ?: null.also {
            logger.info("notfound: offset {}", currentIndex.get())
            currentIndex.set(0)
        }
}
