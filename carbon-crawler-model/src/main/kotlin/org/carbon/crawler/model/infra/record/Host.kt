package org.carbon.crawler.model.infra.record

import org.carbon.crawler.model.extend.exposed.AuditLongEntity
import org.carbon.crawler.model.extend.exposed.AuditLongIdTable
import org.carbon.crawler.model.extend.exposed.DefaultTableSupport
import org.carbon.crawler.model.extend.exposed.EagerTrait
import org.carbon.crawler.model.extend.exposed.TableSupport
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntityClass

/**
 * @author Soda 2018/07/27.
 */
class HostRecord(id: EntityID<Long>) : AuditLongEntity(id, HostTable), EagerTrait {
    companion object :
        LongEntityClass<HostRecord>(HostTable),
        TableSupport<Long> by DefaultTableSupport(HostTable)

    val page by PageRecord referrersOn PageTable.hostId
    val listingQuery by CrawlListQueryRecord referrersOn CrawlListQueryTable.hostId
    val detailQuery by CrawlDetailQueryRecord referrersOn CrawlDetailQueryTable.hostId

    var title by HostTable.title
    var url by HostTable.url
    var memo by HostTable.memo
}

object HostTable : AuditLongIdTable(name = "host") {
    val url = varchar("url", 255)
    val title = varchar("title", 255)
    val memo = text("memo").nullable()
}
