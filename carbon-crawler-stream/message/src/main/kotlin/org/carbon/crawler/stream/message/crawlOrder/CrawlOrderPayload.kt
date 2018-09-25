package org.carbon.crawler.stream.message.crawlOrder

/**
 * @author Soda 2018/08/09.
 */
data class CrawlOrderPayload(
        val dictionaryId: Long,
        val url: String
)