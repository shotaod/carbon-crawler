package org.carbon.crawler.model.infra.record

import org.carbon.crawler.model.extend.exposed.EagerTrait
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

/**
 * @author Soda 2018/07/27.
 */
class CrawlDetailQueryRecord(id: EntityID<Long>) : LongEntity(id), EagerTrait {
    companion object : LongEntityClass<CrawlDetailQueryRecord>(CrawlDetailQueryTable)

    var host by HostRecord referencedOn CrawlDetailQueryTable.hostId
    val queryName by CrawlDetailQueryTable.name
    val query by CrawlDetailQueryTable.query
    val type by CrawlDetailQueryTable.type
}

object CrawlDetailQueryTable : LongIdTable(name = "crawl_detail_query") {
    val hostId = reference("host_id", HostTable)
    val name = varchar("name", 255)
    val query = varchar("query", 255)
    val type = varchar("type", 63)
}
