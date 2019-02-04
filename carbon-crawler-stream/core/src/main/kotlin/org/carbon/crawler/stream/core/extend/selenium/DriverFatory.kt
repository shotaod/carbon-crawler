package org.carbon.crawler.stream.core.extend.selenium

import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * @author Soda 2018/08/05.
 */
private val logger = LoggerFactory.getLogger(DriverFactory::class.java)

private class CloseableDriver(remoteAddress: URL, capabilities: Capabilities) : RemoteWebDriver(remoteAddress, capabilities) {
    fun <R> use(timeout: Long, block: (CloseableDriver) -> R): R {
        logger.info("started web driver with { timeout: $timeout }")
        try {
            this.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS)
            return block(this)
        } catch (e: Throwable) {
            logger.warn("unhandled exception occurred", e)
            throw e
        } finally {
            this.quit()
        }
    }
}

class DriverFactory(
    private val remoteAddress: URL,
    private val capabilities: Capabilities) {
    fun <R> use(c: Capabilities? = null, timeout: Long = 10, block: (RemoteWebDriver) -> R): R =
        CloseableDriver(remoteAddress, capabilities.merge(c)).use(timeout, block)
}
