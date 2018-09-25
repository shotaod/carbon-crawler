package org.carbon.crawler.batch.step.crawle

import org.carbon.crawler.batch.step.crawle.item.PageChunkItem
import org.carbon.crawler.model.DictionaryEntity
import org.carbon.crawler.model.PageAttributeEntity
import org.carbon.crawler.model.PageAttributeTable
import org.carbon.crawler.model.PageTable
import org.jetbrains.exposed.sql.batchInsert
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

/**
 * @author Soda 2018/07/22.
 */
@Component
class PageWriter : ItemWriter<PageChunkItem> {
    fun String.substring(count: Int): String {
        return if (this.length > count) this.substring(0..count)
        else this
    }

    override fun write(items: List<PageChunkItem>) {
        val pages = items.flatMap { it.items.map { item -> it.dictionaryId to item } }
        val pageIds = PageTable
                .batchInsert(pages) { id_page ->
                    val dictionaryId = id_page.first
                    val (url, title) = id_page.second
                    this[PageTable.dictionaryId] = DictionaryEntity.pk(dictionaryId)
                    this[PageTable.url] = url
                    this[PageTable.title] = title
                }
                .map { it[PageTable.id] as Long }
        val attributes = pages
                .mapIndexed { index, pair -> pageIds[index] to pair.second }
                .flatMap { pageId_pageItem ->
                    val pageId = pageId_pageItem.first
                    val (_, _, attributes) = pageId_pageItem.second
                    attributes.map { attribute -> pageId to attribute }
                }
        PageAttributeTable.batchInsert(attributes) { id_attribute ->
            val pageId = id_attribute.first
            val (key, value) = id_attribute.second
            this[PageAttributeTable.pageId] = PageAttributeEntity.pk(pageId)
            this[PageAttributeTable.key] = key
            this[PageAttributeTable.value] = value.toString()
            this[PageAttributeTable.type] = value::class.simpleName?.substring(10) ?: "unknown"
        }
    }
}
