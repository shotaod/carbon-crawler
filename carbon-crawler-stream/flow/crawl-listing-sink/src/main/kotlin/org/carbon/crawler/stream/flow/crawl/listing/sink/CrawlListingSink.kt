package org.carbon.crawler.stream.flow.crawl.listing.sink

import org.carbon.crawler.model.domain.base.ExpeditionID
import org.carbon.crawler.model.domain.base.Snapshots
import org.carbon.crawler.model.domain.repo.ExpeditionRepository
import org.carbon.crawler.model.domain.repo.SnapshotRepository
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.carbon.crawler.stream.core.extend.spring.cloud.SinkFunction
import org.carbon.crawler.stream.message.ExpeditionPayload

fun crawlListingSink(driverFactory: DriverFactory): SinkFunction<ExpeditionPayload> = fun(payload: ExpeditionPayload) {
    val (id) = payload
    val expediter = ExpeditionRepository.fetch(ExpeditionID(id))

    val result = driverFactory.use { driver ->
        expediter.explore(driver)
    }

    val snaps = result.map { res ->
        Snapshots.new(
            res.hostId,
            res.title,
            res.href
        ) { nextId ->
            res.attributes.map {
                SnapshotAttribute(
                    nextId(),
                    it.key,
                    it.value,
                    it.type
                )
            }
        }
    }

    SnapshotRepository.bulkSave(snaps)
}
