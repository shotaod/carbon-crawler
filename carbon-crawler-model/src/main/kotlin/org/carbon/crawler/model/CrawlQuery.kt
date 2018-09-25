package org.carbon.crawler.model

import org.carbon.crawler.model.extend.exposed.EagerTrait
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

/**
 * @author Soda 2018/07/27.
 */
class CrawlQueryEntity(id: EntityID<Long>) : LongEntity(id), EagerTrait {
    companion object : LongEntityClass<CrawlQueryEntity>(CrawlQueryTable)

    var query by CrawlQueryTable.query
    var name by CrawlQueryTable.name
    var crawlRootDocEntity by CrawlRootDocEntity referencedOn CrawlQueryTable.crawlRootDocId
}

object CrawlQueryTable : LongIdTable(name = "crawl_query") {
    val crawlRootDocId = reference("crawl_root_doc_id", CrawlRootDocTable)
    val name = varchar("name", 255)
    val query = varchar("query", 255)
}
