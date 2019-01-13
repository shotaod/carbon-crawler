package org.carbon.crawler.model.infra.meta

import org.carbon.crawler.model.infra.record.CrawlDetailQueryTable
import org.carbon.crawler.model.infra.record.CrawlListQueryTable
import org.carbon.crawler.model.infra.record.HostTable
import org.carbon.crawler.model.infra.record.PageAttributeTable
import org.carbon.crawler.model.infra.record.PageTable

/**
 * @author Soda 2018/08/09.
 */
object Meta {
    val tablesByAncestor = arrayOf(
        HostTable,
        PageTable,
        PageAttributeTable,
        CrawlListQueryTable,
        CrawlDetailQueryTable
    )
}