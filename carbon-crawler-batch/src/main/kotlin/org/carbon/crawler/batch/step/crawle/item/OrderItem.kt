package org.carbon.crawler.batch.step.crawle.item

import org.openqa.selenium.remote.RemoteWebDriver

/**
 * @author Soda 2018/07/29.
 */
data class OrderItem(
        val dictionaryId: Long,
        val url: String,
        val directList: RemoteWebDriver.() -> Unit,
        val range: IntRange,
        val fetchDetailPagePaths: RemoteWebDriver.() -> List<String>,
        val fetchDetailPageAttributes: RemoteWebDriver.() -> Map<String, String>
)
