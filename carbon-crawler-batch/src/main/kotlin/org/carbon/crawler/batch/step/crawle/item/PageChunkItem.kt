package org.carbon.crawler.batch.step.crawle.item

import org.carbon.crawler.batch.step.support.ChunkItem

class PageChunkItem(val dictionaryId: Long) : ChunkItem<PageItem, PageChunkItem>() {
    override fun assign(t: PageItem): PageChunkItem {
        val chunk = PageChunkItem(dictionaryId)
        chunk.items = items.plus(t)
        return chunk
    }
}

data class PageItem(
        val url: String,
        val title: String,
        val attributes: Map<String, Any>
)
