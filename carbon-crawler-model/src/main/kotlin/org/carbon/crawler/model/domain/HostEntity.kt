package org.carbon.crawler.model.domain

import java.nio.file.Paths

/**
 * @author Soda 2018/08/06.
 */
data class HostEntity(
    val id: Long?,
    val url: String,
    val title: String,
    val memo: String?,
    val pages: List<Page>,
    val query: Query
) {
    data class Page(
        val id: Long?,
        val title: String,
        val url: String,
        val attributes: List<PageAttribute>
    )

    data class PageAttribute(
        val id: Long?,
        val key: String,
        val value: String,
        val type: String
    )

    data class Query(
        val id: Long?,
        val listingPagePath: String,
        val listingLinkQuery: String,
        val details: List<DetailQuery>
    )

    data class DetailQuery(
        val id: Long?,
        val queryName: String,
        val query: String,
        val type: String
    )

    val listingUrl get() = Paths.get(url, query.listingPagePath).toString()
    val listingQuery get() = query.listingLinkQuery
    val detailQueries get() = query.details
}