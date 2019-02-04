package org.carbon.crawler.admin.usecase.snap

import org.carbon.crawler.admin.www.Pager
import org.carbon.crawler.admin.www.by
import org.carbon.crawler.admin.www.v1.PagingUrlParameter
import org.carbon.crawler.admin.www.v1.snap.SnapResponse.Companion.parseEntity
import org.carbon.crawler.model.domain.QueryRepository
import org.carbon.crawler.model.extend.kompose.Transaction
import org.carbon.crawler.model.infra.record.HostTable
import org.carbon.kompose.kompose
import org.jetbrains.exposed.sql.selectAll

object SnapUseCase {
    fun fetchSnaps(param: PagingUrlParameter) = kompose(Transaction(logging = true)) {
        val (page, size) = param

        val snaps = QueryRepository.fetch(page, size).map(::parseEntity)
        val count = HostTable.selectAll().count()

        snaps by Pager(page, count, size)
    }
}
