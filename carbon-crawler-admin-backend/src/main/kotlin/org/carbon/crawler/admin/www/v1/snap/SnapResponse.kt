package org.carbon.crawler.admin.www.v1.snap

import org.carbon.crawler.model.domain.HostEntity

data class SnapResponse(
    val id: Long,
    val url: String,
    val title: String,
    val memo: String?,
    val pages: List<PageItem>
) {
    data class PageItem(
        val id: Long,
        val title: String,
        val url: String,
        val attributes: List<PageAttributeItem>
    )

    data class PageAttributeItem(
        val id: Long,
        val key: String,
        val value: String,
        val type: String
    )

    companion object {
        fun parseEntity(entity: HostEntity): SnapResponse = SnapResponse(
            entity.id!!,
            entity.url,
            entity.title,
            entity.memo,
            entity.pages.map { page ->
                PageItem(
                    page.id!!,
                    page.title,
                    page.url,
                    page.attributes.map { attr ->
                        PageAttributeItem(
                            attr.id!!,
                            attr.key,
                            attr.value,
                            attr.type
                        )
                    }
                )
            }
        )
    }
}