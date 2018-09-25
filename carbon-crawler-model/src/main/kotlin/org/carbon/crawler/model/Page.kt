package org.carbon.crawler.model

import org.carbon.crawler.model.extend.exposed.EagerTrait
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

/**
 * @author Soda 2018/07/27.
 */
class PageEntity(id: EntityID<Long>) : LongEntity(id), EagerTrait {
    companion object : LongEntityClass<PageEntity>(PageTable)

    var dicationary by DictionaryEntity referencedOn PageTable.dictionaryId
    var title by PageTable.title
    var url by PageTable.url
    val attributes by PageAttributeEntity
            .referrersOn(PageAttributeTable.pageId).eager(this)
}

object PageTable : LongIdTable(name = "page") {
    val dictionaryId = reference("dictionary_id", DictionaryTable)
    val title = varchar("title", 255)
    val url = varchar("url", 255).uniqueIndex()
}
