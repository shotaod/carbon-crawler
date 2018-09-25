package org.carbon.crawler.model.meta

import org.carbon.crawler.model.CrawlQueryEntity
import org.carbon.crawler.model.CrawlRootDocEntity
import org.carbon.crawler.model.CrawlSourceEntity
import org.carbon.crawler.model.DictionaryEntity
import org.carbon.crawler.model.PageAttributeEntity
import org.carbon.crawler.model.PageEntity
import org.carbon.crawler.model.TagEntity

/**
 * @author Soda 2018/08/09.
 */
object Models {
    val entities = listOf(
            CrawlQueryEntity,
            CrawlRootDocEntity,
            CrawlSourceEntity,
            DictionaryEntity,
            PageEntity,
            PageAttributeEntity,
            TagEntity
    )
}