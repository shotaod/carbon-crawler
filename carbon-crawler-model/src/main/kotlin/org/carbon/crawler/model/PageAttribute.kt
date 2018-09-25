package org.carbon.crawler.model

import org.carbon.crawler.model.extend.exposed.DefaultTableSupport
import org.carbon.crawler.model.extend.exposed.TableSupport
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

/**
 * @author Soda 2018/07/27.
 */
class PageAttributeEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PageAttributeEntity>(PageAttributeTable), TableSupport<Long> by DefaultTableSupport(PageAttributeTable)

    var key by PageAttributeTable.key
    var value by PageAttributeTable.value
    var type by PageAttributeTable.type
    //var page by PageEntity referencedOn PageAttributeTable.pageId
}

object PageAttributeTable : LongIdTable(name = "page_attribute") {
    val pageId = reference("page_id", PageTable)
    val key = varchar("key", 255)
    val value = text("value")
    val type = varchar("type", 10)
}
