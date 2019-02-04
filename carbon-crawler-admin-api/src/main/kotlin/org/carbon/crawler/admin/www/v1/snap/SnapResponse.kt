package org.carbon.crawler.admin.www.v1.snap

import org.carbon.crawler.model.domain.SnapShotEntity

data class SnapResponse(
    val id: Long,
    val title: String,
    val url: String,
    val attributes: List<PageAttributeItem>
) {

    data class PageAttributeItem(
        val id: Long,
        val key: String,
        val value: String,
        val type: String
    )

    companion object {
        fun parseEntity(entity: SnapShotEntity): SnapResponse = SnapResponse(
            entity.id!!,
            entity.url,
            entity.title,
            entity.attributes.map { attr ->
                PageAttributeItem(
                    attr.id!!,
                    attr.key,
                    attr.value,
                    attr.type
                )
            }
        )
    }
}