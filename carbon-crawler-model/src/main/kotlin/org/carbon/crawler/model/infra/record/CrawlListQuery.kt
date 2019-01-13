package org.carbon.crawler.model.infra.record

import org.carbon.crawler.model.extend.exposed.AuditLongEntity
import org.carbon.crawler.model.extend.exposed.AuditLongIdTable
import org.carbon.crawler.model.extend.exposed.EagerTrait
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntityClass

/**
 * @author Soda 2018/07/27.
 */
class CrawlListQueryRecord(id: EntityID<Long>) : AuditLongEntity(id, CrawlListQueryTable), EagerTrait {
    companion object : LongEntityClass<CrawlListQueryRecord>(CrawlListQueryTable)

    var host by HostRecord referencedOn CrawlListQueryTable.hostId
    var listingPagePath by CrawlListQueryTable.listingPagePath
    var listingLinkQuery by CrawlListQueryTable.listingLinkQuery
}

object CrawlListQueryTable : AuditLongIdTable(name = "crawl_list_query") {
    val hostId = reference("host_id", HostTable).uniqueIndex()
    val listingPagePath = varchar("listing_page_path", 255)
    val listingLinkQuery = varchar("listing_link_query", 1023)
}
