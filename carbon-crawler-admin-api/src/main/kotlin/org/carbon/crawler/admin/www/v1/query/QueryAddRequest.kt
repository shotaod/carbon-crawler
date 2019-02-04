package org.carbon.crawler.admin.www.v1.query

import org.carbon.crawler.admin.extend.carbon.validation.LengthMax
import org.carbon.crawler.admin.extend.carbon.validation.OneOf
import org.carbon.crawler.admin.extend.carbon.validation.XPath
import org.carbon.crawler.model.domain.QueryEntity
import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.URL
import org.carbon.objects.validation.matcher.and
import org.carbon.objects.validation.matcher.be
import org.carbon.objects.validation.matcher.max
import org.carbon.objects.validation.matcher.mayBe

data class QueryAddRequest(
    val url: String,
    val title: String,
    val memo: String?,
    val listing: ListingQuery,
    val details: List<DetailQuery>
) : Validated<QueryAddRequest> by QueryAddRequestSchema {
    fun toEntity(id: Long? = null): QueryEntity = QueryEntity(
        id,
        url,
        title,
        memo,
        QueryEntity.Query(
            null,
            listing.pagePath,
            listing.linkQuery,
            details.map {
                QueryEntity.DetailQuery(
                    null,
                    it.queryName,
                    it.query,
                    it.type
                )
            }
        )
    )
}

object QueryAddRequestSchema : Validated<QueryAddRequest> {
    override val def: Definition<QueryAddRequest> = { body ->
        body.url should { it be URL } otherwise "url".invalidate()
        body.title should { it max 255 } otherwise "title".invalidate()
        body.memo should { it mayBe LengthMax(1023) } otherwise "memo".invalidate()

        body.listing.shouldValidated() otherwise "listing".invalidate()
        body.details.shouldEachValidated() otherwise "details".invalidate()
    }
}

data class ListingQuery(
    val pagePath: String,
    val linkQuery: String
) : Validated<ListingQuery> by ListingSchema

object ListingSchema : Validated<ListingQuery> {
    override val def: Definition<ListingQuery> = { query ->
        query.pagePath should { it max 255 } otherwise "pagePath".invalidate()
        query.linkQuery should { it be XPath } otherwise "linkQuery".invalidate()
    }
}

data class DetailQuery(
    val queryName: String,
    val query: String,
    val type: String
) : Validated<DetailQuery> by DetailSchema

object DetailSchema : Validated<DetailQuery> {
    override val def: Definition<DetailQuery> = { query ->
        query.queryName should { it max 255 } otherwise "name".invalidate()
        query.query should { and(it max 255, it be XPath) } otherwise "query".invalidate()
        query.type should { it be OneOf("text/text", "image/text") } otherwise "type".invalidate()
    }
}
