package org.carbon.crawler.model

import org.carbon.crawler.model.extend.exposed.EagerTrait
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

/**
 * @author Soda 2018/07/27.
 */
class CrawlRootDocEntity(id: EntityID<Long>) : LongEntity(id), EagerTrait {
    companion object : LongEntityClass<CrawlRootDocEntity>(CrawlRootDocTable)

    var listPagePath by CrawlRootDocTable.listPagePath
    var listHolderQuery by CrawlRootDocTable.listHolderQuery
    var listItemQuery by CrawlRootDocTable.listItemQuery
    var dictionary by DictionaryEntity referencedOn CrawlRootDocTable.dictionaryId
    val attributes by CrawlQueryEntity
            .referrersOn(CrawlQueryTable.crawlRootDocId)
            .eager(this)
    val sources by CrawlSourceEntity
            .referrersOn(CrawlSourceTable.crawlRootDocId)
            .eager(this)
}

object CrawlRootDocTable : LongIdTable(name = "crawl_root_doc") {
    val dictionaryId = reference("dictionary_id", DictionaryTable)
    val listPagePath = varchar("list_page_path", 255)
    val listHolderQuery = varchar("list_holder_query", 1023)
    val listItemQuery = varchar("list_item_query", 1023)
}
