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

object QueryRepository {
    fun save(entity: QueryEntity) {
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

    private fun doSave(id: EntityID<Long>, entity: QueryEntity) {
        CrawlListQueryTable.insert {
            it[hostId] = id
            it[listingPagePath] = entity.query.listingPagePath
            it[listingLinkQuery] = entity.query.listingLinkQuery
        }
        CrawlDetailQueryTable.batchInsert(entity.query.details) { data ->
            this[CrawlDetailQueryTable.hostId] = id
            this[CrawlDetailQueryTable.name] = data.queryName
            this[CrawlDetailQueryTable.query] = data.query
            this[CrawlDetailQueryTable.type] = data.type
        }
    }

    fun fetchBy(id: Long): QueryEntity? {
        val result = doFetch(listOf(id))
        return result.firstOrNull()
    }

    fun fetch(page: Int, size: Int): List<QueryEntity> {
        val ids = HostTable
            .slice(HostTable.id)
            .selectAll()
            .limit(size, page)
            .map { it[HostTable.id].value }
        return doFetch(ids)
    }

    private fun doFetch(ids: List<Long>): List<QueryEntity> {
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
                ?.also {
                    hostRecords[it.value] = HostRecord.wrapRow(row)
                }
            row.tryGet(PageTable.id)
                ?.also {
                    val record = PageRecord.wrapRow(row)
                    pageRecords
                        .getOrPut(record.host.id.value) { HashMap() }
                        .putIfAbsent(record.id.value, record)
                }
            row.tryGet(PageAttributeTable.id)
                ?.also {
                    val record = PageAttributeRecord.wrapRow(row)
                    pageAttributeRecords
                        .getOrPut(record.page.id.value) { HashMap() }
                        .putIfAbsent(record.id.value, record)
                }
            row.tryGet(CrawlListQueryTable.id)
                ?.also {
                    val record = CrawlListQueryRecord.wrapRow(row)
                    listQueryRecords
                        .putIfAbsent(record.host.id.value, record)
                }
            row.tryGet(CrawlDetailQueryTable.id)
                ?.also {
                    val record = CrawlDetailQueryRecord.wrapRow(row)
                    detailQueryRecords
                        .getOrPut(record.host.id.value) { HashMap() }
                        .putIfAbsent(record.id.value, record)
                }
        }

        val result: List<QueryEntity>
            get() {
                return hostRecords.map { (id, record) ->
                    val detailQueries = detailQueryRecords[id]
                        ?.map { (qId, qRecord) ->
                            QueryEntity.DetailQuery(
                                qId,
                                qRecord.queryName,
                                qRecord.query,
                                qRecord.type
                            )
                        } ?: emptyList()
                    val query = listQueryRecords[id]
                        ?.let {
                            QueryEntity.Query(
                                it.id.value,
                                it.listingPagePath,
                                it.listingLinkQuery,
                                detailQueries
                            )
                        } ?: {
                        throw IllegalStateException("data is illegal")
                    }()
                    QueryEntity(
                        id,
                        record.url,
                        record.title,
                        record.memo,
                        query
                    )
                }
            }
    }
}