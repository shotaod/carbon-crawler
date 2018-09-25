package org.carbon.crawler.batch.extend.selenium

import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL

/**
 * @author Soda 2018/08/05.
 */
class CloseableDriver(remoteAddress: URL, capabilities: Capabilities) : RemoteWebDriver(remoteAddress, capabilities) {
    inline fun <R> use(block: (CloseableDriver) -> R): R {
        try {
            return block(this)
        } catch (e: Throwable) {
            throw e
        } finally {
            this.quit()
        }
    }
}

class DriverFactory(
        private val remoteAddress: URL,
        private val capabilities: Capabilities) {
    fun setupDriver(c: Capabilities? = null): CloseableDriver = CloseableDriver(remoteAddress, capabilities.merge(c))
}
