package org.carbon.crawler.admin.usecase.snap

import org.carbon.composer.compose
import org.carbon.crawler.admin.www.Pager
import org.carbon.crawler.admin.www.by
import org.carbon.crawler.admin.www.v1.PagingUrlParameter
import org.carbon.crawler.admin.www.v1.snap.SnapResponse.Companion.parseEntity
import org.carbon.crawler.model.domain.HostRepository
import org.carbon.crawler.model.extend.composer.Transaction
import org.carbon.crawler.model.infra.record.HostTable
import org.jetbrains.exposed.sql.select

object SnapUseCase {
    fun fetchSnaps(param: PagingUrlParameter) = compose(Transaction(logging = true)) {
        val (page, size) = param

        val snaps = HostRepository.fetch(page, size).map(::parseEntity)
        val count = org.carbon.crawler.model.infra.record.HostTable.select { HostTable.deletedAt.isNull() }.count()

        snaps by Pager(page, count, size)
    }
}
