package org.carbon.crawler.batch.step.crawle

import org.carbon.crawler.batch.step.crawle.item.DictionaryItem
import org.carbon.crawler.model.DictionaryTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.springframework.batch.item.ItemReader
import org.springframework.stereotype.Component

/**
 * @author Soda 2018/07/22.
 */
@Component
class DictionaryReader : ItemReader<DictionaryItem> {
    private var offset = 0
    var size = 100
    private val mutableList: MutableList<DictionaryItem> = mutableListOf()

    override fun read(): DictionaryItem? {
        if (mutableList.isNotEmpty()) {
            return mutableList.removeAt(0)
        }

        mutableList.addAll(fetchNext())

        if (mutableList.isEmpty()) {
            offset = 0
            return null
        }

        return mutableList.removeAt(0)
    }

    private fun fetchNext(): MutableList<DictionaryItem> {
        val res = DictionaryTable.selectAll()
                .orderBy(DictionaryTable.id to SortOrder.ASC)
                .limit(size, offset = offset)
                .map { it.toItem() }.toMutableList()
        offset += size
        return res
    }

    fun ResultRow.toItem(): DictionaryItem = DictionaryItem(this[DictionaryTable.id].value, this[DictionaryTable.url])
}
