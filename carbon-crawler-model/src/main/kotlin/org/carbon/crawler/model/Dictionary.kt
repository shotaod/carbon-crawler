package org.carbon.crawler.model

import org.carbon.crawler.model.extend.exposed.DefaultTableSupport
import org.carbon.crawler.model.extend.exposed.EagerTrait
import org.carbon.crawler.model.extend.exposed.TableSupport
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

/**
 * @author Soda 2018/07/27.
 */
class DictionaryEntity(id: EntityID<Long>) : LongEntity(id), EagerTrait {
    companion object : LongEntityClass<DictionaryEntity>(DictionaryTable), TableSupport<Long> by DefaultTableSupport(DictionaryTable)

    var title by DictionaryTable.title
    var url by DictionaryTable.url
    var memo by DictionaryTable.memo
    val pages by PageEntity referrersOn PageTable.dictionaryId
    val tags by TagEntity referrersOn TagTable.dictionaryId
}

object DictionaryTable : LongIdTable(name = "dictionary") {
    val url = varchar("url", 255)
    val title = varchar("title", 255)
    val memo = text("memo").nullable()
}
