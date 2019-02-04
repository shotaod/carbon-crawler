package org.carbon.crawler.model.domain

/**
 * @author Soda 2018/08/06.
 */
data class SnapShotEntity(
    val id: Long?,
    val hostId: Long,
    val title: String,
    val url: String,
    val attributes: List<PageAttribute>
) {
    data class PageAttribute(
        val id: Long?,
        val key: String,
        val value: String,
        val type: String
    )
}