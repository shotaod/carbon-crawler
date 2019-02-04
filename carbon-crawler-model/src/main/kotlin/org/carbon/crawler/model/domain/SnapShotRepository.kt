package org.carbon.crawler.model.domain

import org.carbon.crawler.model.extend.exposed.batchInsertV2
import org.carbon.crawler.model.infra.record.HostTable
import org.carbon.crawler.model.infra.record.PageAttributeRecord
import org.carbon.crawler.model.infra.record.PageAttributeTable
import org.carbon.crawler.model.infra.record.PageRecord
import org.carbon.crawler.model.infra.record.PageTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

object SnapShotRepository {
    fun save(entity: SnapShotEntity) {
        when (entity.id) {
            null -> {
                val entityId = PageTable.insertAndGetId {
                    it[hostId] = EntityID(entity.hostId, HostTable)
                    it[title] = entity.title
                    it[url] = entity.url
                }
                doSave(entityId, entity)
            }
            else -> {
                val entityID = EntityID(entity.id, PageTable)
                PageTable.update({ PageTable.id eq entity.id }) {
                    it[title] = entity.title
                    it[url] = entity.url
                }
                PageAttributeTable.deleteWhere {
                    PageAttributeTable.pageId eq entityID
                }
                doSave(entityID, entity)
            }
        }
    }

    private fun doSave(id: EntityID<Long>, entity: SnapShotEntity) {
        val now = LocalDateTime.now()
        PageAttributeTable.batchInsertV2(
            data = entity.attributes,
            onDuplicate = {
                this[PageAttributeTable.updatedAt] = now
            },
            body = {
                this[PageAttributeTable.pageId] = id
                this[PageAttributeTable.key] = it.key
                this[PageAttributeTable.value] = it.value
                this[PageAttributeTable.type] = it.type
            })
    }

    fun fetchBy(id: Long): SnapShotEntity? {
        val result = doFetch(listOf(id))
        return result.firstOrNull()
    }

    fun fetch(page: Int, size: Int): List<SnapShotEntity> {
        val ids = PageTable
            .slice(PageTable.id)
            .selectAll()
            .limit(size, page)
            .map { it[PageTable.id].value }
        return doFetch(ids)
    }

    private fun doFetch(ids: List<Long>): List<SnapShotEntity> {
        val rows = PageTable
            .leftJoin(PageAttributeTable, { id }, { pageId })
            .select { PageTable.id.inList(ids) }
            .orderBy(PageTable.id to true)
            .toList()
        return parse(rows)
    }

    private fun parse(rows: List<ResultRow>): List<SnapShotEntity> {
        val pageMap = mutableMapOf<Long, PageRecord>()
        val attributeMap = mutableMapOf<Long, PageAttributeRecord>()

        rows.forEach { row ->
            row.tryGet(PageTable.id)
                ?.also { id -> pageMap[id.value] = PageRecord.wrapRow(row) }
            row.tryGet(PageAttributeTable.id)
                ?.also { id -> attributeMap[id.value] = PageAttributeRecord.wrapRow(row) }
        }

        return pageMap.map { (id, pageRecord) ->
            SnapShotEntity(
                id,
                pageRecord.host.id.value,
                pageRecord.title,
                pageRecord.url,
                pageRecord.attributes
                    .map { attributeMap[it.id.value]!! }
                    .map {
                        SnapShotEntity.PageAttribute(
                            it.id.value,
                            it.key,
                            it.value,
                            it.type
                        )
                    })
        }
    }
}