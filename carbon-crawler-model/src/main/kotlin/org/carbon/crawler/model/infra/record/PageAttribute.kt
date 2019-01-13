package org.carbon.crawler.model.infra.record

import org.carbon.crawler.model.extend.exposed.AuditLongIdTable
import org.carbon.crawler.model.extend.exposed.DefaultTableSupport
import org.carbon.crawler.model.extend.exposed.TableSupport
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

/**
 * @author Soda 2018/07/27.
 */
class PageAttributeRecord(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PageAttributeRecord>(PageAttributeTable), TableSupport<Long> by DefaultTableSupport(PageAttributeTable)

    var page by PageRecord referencedOn PageAttributeTable.pageId
    var key by PageAttributeTable.key
    var value by PageAttributeTable.value
    var type by PageAttributeTable.type
}

object PageAttributeTable : AuditLongIdTable(name = "page_attribute") {
    val pageId = reference("page_id", PageTable)
    val key = varchar("key", 255)
    val value = text("value")
    val type = varchar("type", 10)
}
