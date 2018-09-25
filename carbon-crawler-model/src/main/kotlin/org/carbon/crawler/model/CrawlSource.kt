package org.carbon.crawler.model

import org.carbon.crawler.model.extend.exposed.AuditLongEntity
import org.carbon.crawler.model.extend.exposed.AuditLongIdTable
import org.carbon.crawler.model.extend.exposed.DefaultTableSupport
import org.carbon.crawler.model.extend.exposed.TableSupport
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntityClass

/**
 * @author Soda 2018/07/27.
 */
class CrawlSourceEntity(id: EntityID<Long>) : AuditLongEntity(id, CrawlSourceTable) {
    companion object : LongEntityClass<CrawlSourceEntity>(CrawlSourceTable), TableSupport<Long> by DefaultTableSupport(CrawlSourceTable)

    var url by CrawlSourceTable.url
    var rootDoc by CrawlRootDocEntity referencedOn CrawlSourceTable.crawlRootDocId
}

object CrawlSourceTable : AuditLongIdTable(name = "crawl_source") {
    val crawlRootDocId = reference("crawl_root_doc_id", CrawlRootDocTable)
    val url = varchar("url", 768)
}
