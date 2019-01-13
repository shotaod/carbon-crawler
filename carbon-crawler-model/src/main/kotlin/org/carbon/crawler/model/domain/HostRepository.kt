package org.carbon.crawler.model.domain

import org.carbon.crawler.model.infra.record.CrawlDetailQueryRecord
import org.carbon.crawler.model.infra.record.CrawlDetailQueryTable
import org.carbon.crawler.model.infra.record.CrawlListQueryRecord
import org.carbon.crawler.model.infra.record.CrawlListQueryTable
import org.carbon.crawler.model.infra.record.HostRecord
import org.carbon.crawler.model.infra.record.HostTable
import org.carbon.crawler.model.infra.record.PageAttributeRecord
import org.carbon.crawler.model.infra.record.PageAttributeTable
import org.carbon.crawler.model.infra.record.PageRecord
import org.carbon.crawler.model.infra.record.PageTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

object HostRepository {
    fun save(entity: HostEntity) {
        when (entity.id) {
            null -> {
                val entityID = HostTable.insertAndGetId {
                    it[title] = entity.title
                    it[url] = entity.url
                    it[memo] = entity.memo
                }
                doSave(entityID, entity)
            }
            else -> {
                CrawlDetailQueryTable.deleteWhere {
                    CrawlDetailQueryTable.hostId eq EntityID(entity.id, HostTable)
                }
                CrawlListQueryTable.deleteWhere {
                    CrawlListQueryTable.hostId eq EntityID(entity.id, HostTable)
                }
                val entityID = EntityID(entity.id, HostTable)
                HostTable.update({
                    HostTable.id eq entityID
                }) {
                    it[title] = entity.title
                    it[url] = entity.url
                    it[memo] = entity.memo
                }
                doSave(entityID, entity)
            }
        }
    }

    private fun doSave(id: EntityID<Long>, entity: HostEntity) {
        CrawlListQueryTable.insert {
            it[hostId] = id
            it[listingPagePath] = entity.query.listingPagePath
            it[listingLinkQuery] = entity.query.listingLinkQuery
        }
        entity.query.details
            .map {
                it.queryName to it.query
            }
            .let {
                CrawlDetailQueryTable.batchInsert(it) { data ->
                    this[CrawlDetailQueryTable.hostId] = id
                    this[CrawlDetailQueryTable.name] = data.first
                    this[CrawlDetailQueryTable.query] = data.second
                }
            }
    }

    fun fetchBy(id: Long): HostEntity? {
        val result = doFetch(listOf(id))
        return if (result.isEmpty()) null
        else result[0]
    }

    fun fetch(page: Int, size: Int): List<HostEntity> {

        val ids = HostTable
            .slice(HostTable.id)
            .selectAll()
            .limit(size, page)
            .map { it[HostTable.id].value }
        return doFetch(ids)
    }

    private fun doFetch(ids: List<Long>): List<HostEntity> {
        val parser = RowParser()
        val records = HostTable
            .leftJoin(PageTable)
            .leftJoin(PageAttributeTable, { PageTable.id }, { pageId })
            .leftJoin(CrawlListQueryTable)
            .leftJoin(CrawlDetailQueryTable)
            .select {
                HostTable.id.inList(ids)
            }
            .orderBy(HostTable.id to true)
            .toList()
        records
            .forEach(parser::parse)
        return parser.result
    }

    private class RowParser {
        val hostRecords = HashMap<Long, HostRecord>()
        // hostId to recordId to records
        val pageRecords = HashMap<Long, MutableMap<Long, PageRecord>>()
        val pageAttributeRecords = HashMap<Long, MutableMap<Long, PageAttributeRecord>>()
        val listQueryRecords = HashMap<Long, CrawlListQueryRecord>()
        val detailQueryRecords = HashMap<Long, MutableMap<Long, CrawlDetailQueryRecord>>()

        fun parse(row: ResultRow) {
            row.tryGet(HostTable.id)
                ?.let {
                    hostRecords[it.value] = HostRecord.wrapRow(row)
                }
            row.tryGet(PageTable.id)
                ?.let {
                    val record = PageRecord.wrapRow(row)
                    pageRecords
                        .computeIfAbsent(record.host.id.value) { HashMap() }
                        .putIfAbsent(record.id.value, record)
                }
            row.tryGet(PageAttributeTable.id)
                ?.let {
                    val record = PageAttributeRecord.wrapRow(row)
                    pageAttributeRecords
                        .computeIfAbsent(record.page.id.value) { HashMap() }
                        .putIfAbsent(record.id.value, record)
                }
            row.tryGet(CrawlListQueryTable.id)
                ?.let {
                    val record = CrawlListQueryRecord.wrapRow(row)
                    listQueryRecords
                        .putIfAbsent(record.host.id.value, record)
                }
            row.tryGet(CrawlDetailQueryTable.id)
                ?.let {
                    val record = CrawlDetailQueryRecord.wrapRow(row)
                    detailQueryRecords
                        .computeIfAbsent(record.host.id.value) { HashMap() }
                        .putIfAbsent(record.id.value, record)
                }
        }

        val result: List<HostEntity>
            get() {
                return hostRecords.map { (id, record) ->
                    val pages = pageRecords[id]
                        ?.map { (pId, pRecord) ->
                            val attributes = (pageAttributeRecords[pId]
                                ?.values
                                ?.map {
                                    HostEntity.PageAttribute(
                                        it.id.value,
                                        it.key,
                                        it.value,
                                        it.type
                                    )
                                } ?: emptyList())
                            HostEntity.Page(
                                pId,
                                pRecord.title,
                                pRecord.url,
                                attributes
                            )
                        } ?: emptyList()


                    val detailQueries = detailQueryRecords[id]
                        ?.map { (qId, qRecord) ->
                            HostEntity.DetailQuery(
                                qId,
                                qRecord.queryName,
                                qRecord.query,
                                qRecord.type
                            )
                        } ?: emptyList()
                    val query = listQueryRecords[id]
                        ?.let {
                            HostEntity.Query(
                                it.id.value,
                                it.listingPagePath,
                                it.listingLinkQuery,
                                detailQueries
                            )
                        } ?: {
                        throw IllegalStateException("data is illegal")
                    }()
                    HostEntity(
                        id,
                        record.url,
                        record.title,
                        record.memo,
                        pages,
                        query
                    )
                }
            }
    }
}