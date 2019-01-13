package org.carbon.crawler.admin.www.v1.query

import org.carbon.crawler.model.domain.HostEntity

data class QueryResponse(
    val id: Long,
    val url: String,
    val title: String,
    val memo: String?,
    val listing: ListingItem,
    val details: List<DetailItem>
) {
    data class ListingItem(
        val pagePath: String,
        val linkQuery: String
    )

    data class DetailItem(
        val queryName: String,
        val query: String,
        val type: String
    )

    companion object {
        fun parseEntity(entity: HostEntity): QueryResponse = QueryResponse(
            entity.id!!,
            entity.url,
            entity.title,
            entity.memo,
            ListingItem(
                entity.query.listingPagePath,
                entity.query.listingLinkQuery
            ),
            entity.query.details.map {
                DetailItem(
                    it.queryName,
                    it.query,
                    it.type
                )
            }
        )
    }
}