package org.carbon.crawler.batch.step.crawle

import org.carbon.crawler.batch.extend.selenium.DriverFactory
import org.carbon.crawler.batch.step.crawle.item.OrderItem
import org.carbon.crawler.batch.step.crawle.item.PageChunkItem
import org.carbon.crawler.batch.step.crawle.item.PageItem
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component
import java.net.URI

/**
 * @author Soda 2018/07/22.
 */
@Component
class CrawlScrapingProcessor(
        val driverFactory: DriverFactory
) : ItemProcessor<OrderItem, PageChunkItem> {
    companion object {
        val log: Logger = LoggerFactory.getLogger(CrawlScrapingProcessor::class.java)
    }

    override fun process(item: OrderItem): PageChunkItem {
        val (id, url, directToList, range, fetchItemLinks, applyDetailInfoOrders) = item
        return driverFactory.setupDriver().use { driver ->
            driver.get(url)
            driver.directToList()
            driver.fetchItemLinks()
                    .subList(range.first, range.last)
                    .mapIndexed { i, path ->
                        driver.get(URI(driver.currentUrl).resolve(path).toString())
                        val pageUrl = driver.currentUrl
                        val pageTitle = driver.title
                        val attributes = try {
                            driver.applyDetailInfoOrders()
                        } catch (e: Throwable) {
                            mapOf("error-$i" to "${e::class.qualifiedName}: ${e.message ?: "unknown"}")
                        }
                        PageItem(pageUrl, pageTitle, attributes)
                    }
                    .fold(PageChunkItem(id), PageChunkItem::assign)
        }
    }
}
