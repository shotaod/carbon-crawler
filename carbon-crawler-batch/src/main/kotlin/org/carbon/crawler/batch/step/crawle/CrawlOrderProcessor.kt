package org.carbon.crawler.batch.step.crawle

import org.carbon.crawler.batch.step.crawle.item.DictionaryItem
import org.carbon.crawler.batch.step.crawle.item.OrderItem
import org.carbon.crawler.model.CrawlRootDocEntity
import org.carbon.crawler.model.CrawlRootDocTable
import org.openqa.selenium.By
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.net.URI

@Component
class CrawlOrderProcessor : ItemProcessor<DictionaryItem, OrderItem> {
    companion object {
        val log: Logger = LoggerFactory.getLogger(CrawlOrderProcessor::class.java)
    }

    override fun process(item: DictionaryItem): OrderItem {
        val entity = CrawlRootDocEntity
                .find { CrawlRootDocTable.dictionaryId eq item.id }
                .single()

        return OrderItem(
                dictionaryId = entity.id.value,
                url = item.url,
                directList = {
                    get(URI(item.url).resolve(entity.listPagePath).toString())
                },
                range = IntRange(0, 10),
                fetchDetailPagePaths = {
                    this
                            .findElement(entity.listHolderQuery.toBy())
                            .findElements(entity.listItemQuery.toBy())
                            .map { it.getAttribute("href") }
                },
                fetchDetailPageAttributes = {
                    entity.attributes
                            .map {
                                val key = it.name
                                val value = findElement(it.query.toBy()).text
                                key to value
                            }.toMap()
                }
        )
    }

    private fun String.toBy(): By {
        val xPathProtocol = "xpath:"
        if (this.startsWith(xPathProtocol)) return By.ByXPath(this.removePrefix(xPathProtocol))

        throw UnsupportedOperationException()
    }
}
