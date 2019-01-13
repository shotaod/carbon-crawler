package org.carbon.crawler.admin.usecase.query

import org.carbon.composer.compose
import org.carbon.crawler.admin.extend.PersistentErrorHandler
import org.carbon.crawler.admin.www.Pager
import org.carbon.crawler.admin.www.by
import org.carbon.crawler.admin.www.v1.PagingUrlParameter
import org.carbon.crawler.admin.www.v1.query.QueryAddRequest
import org.carbon.crawler.admin.www.v1.query.QueryResponse.Companion.parseEntity
import org.carbon.crawler.model.domain.HostRepository
import org.carbon.crawler.model.extend.composer.Transaction
import org.carbon.crawler.model.infra.record.HostTable
import org.jetbrains.exposed.sql.select

/**
 * @author Soda 2018/08/06.
 */
object QueryUseCase {
    fun fetchQueries(param: PagingUrlParameter) = compose(Transaction(logging = true)) {
        val (page, size) = param

        val queries = HostRepository.fetch(page, size).map(::parseEntity)
        val count = HostTable.select { HostTable.deletedAt.isNull() }.count()

        queries by Pager(page, count, size)
    }

    fun saveHost(body: QueryAddRequest) = compose(PersistentErrorHandler(), Transaction(logging = true)) {
        val entity = body.toEntity()
        HostRepository.save(entity)
    }

    fun updateQuery(id: Long, body: QueryAddRequest) = compose(PersistentErrorHandler(), Transaction(logging = true)) {
        val entity = body.toEntity(id)
        HostRepository.save(entity)
    }
}
