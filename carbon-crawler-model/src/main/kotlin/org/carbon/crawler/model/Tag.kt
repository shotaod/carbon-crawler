package org.carbon.crawler.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

/**
 * @author Soda 2018/07/27.
 */
class TagEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TagEntity>(TagTable)

    var name by TagTable.name
    var dictionary by DictionaryEntity referencedOn TagTable.dictionaryId
}

object TagTable : LongIdTable(name = "tag") {
    val dictionaryId = reference("dictionary_id", DictionaryTable)
    val name = varchar("name", 255)
}
