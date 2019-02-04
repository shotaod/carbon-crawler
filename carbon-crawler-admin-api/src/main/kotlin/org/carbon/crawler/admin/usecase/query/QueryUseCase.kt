package org.carbon.crawler.admin.usecase.query

import org.carbon.crawler.admin.extend.kompose.PersistentErrorHandler
import org.carbon.crawler.admin.www.Pager
import org.carbon.crawler.admin.www.by
import org.carbon.crawler.admin.www.v1.PagingUrlParameter
import org.carbon.crawler.admin.www.v1.query.QueryAddRequest
import org.carbon.crawler.admin.www.v1.query.QueryResponse.Companion.parseEntity
import org.carbon.crawler.model.domain.QueryRepository
import org.carbon.crawler.model.extend.kompose.Transaction
import org.carbon.crawler.model.infra.record.HostTable
import org.carbon.kompose.kompose
import org.jetbrains.exposed.sql.selectAll

/**
 * @author Soda 2018/08/06.
 */
object QueryUseCase {
    fun fetchQueries(param: PagingUrlParameter) = kompose(Transaction(logging = true)) {
        val (page, size) = param

        val queries = QueryRepository.fetch(page, size).map(::parseEntity)
        val count = HostTable.selectAll().count()

        queries by Pager(page, count, size)
    }

    fun saveHost(body: QueryAddRequest) = kompose(PersistentErrorHandler(), Transaction(logging = true)) {
        val entity = body.toEntity()
        QueryRepository.save(entity)
    }

    fun updateQuery(id: Long, body: QueryAddRequest) = kompose(PersistentErrorHandler(), Transaction(logging = true)) {
        val entity = body.toEntity(id)
        QueryRepository.save(entity)
    }
}
