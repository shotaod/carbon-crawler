package org.carbon.crawler.stream.integration.extend.kompose

import fi.iki.elonen.NanoHTTPD
import org.carbon.crawler.stream.integration.FsHtmlServer
import org.carbon.kompose.Komposable
import org.slf4j.LoggerFactory

class ServeHtml<T>(private val port: Int) : Komposable<T>() {
    private val logger = LoggerFactory.getLogger(ServeHtml::class.java)

    override fun invoke(): T {
        val htmlServer = FsHtmlServer(port)

        return try {
            htmlServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            logger.info("html server started at: http://localhost:{}", port)
            super.callChild()
        } finally {
            htmlServer.stop()
            logger.info("html server stopped")
        }
    }
}