package org.carbon.crawler.stream.core.extend.selenium

import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * @author Soda 2018/08/05.
 */
class CloseableDriver(remoteAddress: URL, capabilities: Capabilities) : RemoteWebDriver(remoteAddress, capabilities) {
    inline fun <R> use(timeout: Long, block: (CloseableDriver) -> R): R {
        try {
            this.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS)
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
    fun <R> use(c: Capabilities? = null, timeout: Long = 10, block: (CloseableDriver) -> R): R =
            CloseableDriver(remoteAddress, capabilities.merge(c)).use(timeout, block)
}
