package org.carbon.crawler.model.infra.record

import org.carbon.crawler.model.extend.exposed.AuditLongEntity
import org.carbon.crawler.model.extend.exposed.AuditLongIdTable
import org.carbon.crawler.model.extend.exposed.EagerTrait
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntityClass

/**
 * @author Soda 2018/07/27.
 */
class PageRecord(id: EntityID<Long>) : AuditLongEntity(id, PageTable), EagerTrait {
    companion object : LongEntityClass<PageRecord>(PageTable)

    var host by HostRecord referencedOn PageTable.hostId
    val attributes by PageAttributeRecord referrersOn PageAttributeTable.pageId

    var title by PageTable.title
    var url by PageTable.url
}

object PageTable : AuditLongIdTable(name = "page") {
    val hostId = reference("host_id", HostTable)
    val title = varchar("title", 255)
    val url = varchar("url", 255).uniqueIndex()
}
