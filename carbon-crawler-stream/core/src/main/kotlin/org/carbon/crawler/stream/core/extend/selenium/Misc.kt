package org.carbon.crawler.stream.core.extend.selenium

import org.openqa.selenium.By

/**
 * @author Soda 2018/08/10.
 */
fun String.toBy(): By {
    val xPathProtocol = "xpath://"
    if (this.startsWith(xPathProtocol)) return By.ByXPath(this.removePrefix(xPathProtocol))

    throw UnsupportedOperationException()
}