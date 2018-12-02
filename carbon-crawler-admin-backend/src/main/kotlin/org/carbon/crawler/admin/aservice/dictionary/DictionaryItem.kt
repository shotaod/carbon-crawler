package org.carbon.crawler.admin.aservice.dictionary

/**
 * @author Soda 2018/08/06.
 */
data class DictionaryItem(
        val id: Long,
        val url: String,
        val title: String,
        val memo: String?
)