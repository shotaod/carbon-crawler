package org.carbon.crawler.stream.integration

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND
import fi.iki.elonen.NanoHTTPD.Response.Status.OK
import fi.iki.elonen.util.ServerRunner
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Soda 2018/08/03.
 */

// ______________________________________________________
//
// @ convenient entry point
fun main(args: Array<String>) {
    ServerRunner.executeInstance(FsHtmlServer(8080))
}

class FsHtmlServer(port: Int) : NanoHTTPD(port) {
    private val log = LoggerFactory.getLogger(FsHtmlServer::class.java)
    private val basePath = FsHtmlServer::class.java.getResource("/hosts/carbon_wiki").path

    override fun serve(session: IHTTPSession): Response {
        val path = Paths.get(basePath, session.uri)
        log.info("receive: ${session.uri} --> serve html: $path")
        if (!Files.exists(path)) return newFixedLengthResponse(NOT_FOUND, "text/plain", "not found")
        return newFixedLengthResponse(OK, "text/html", Files.readAllLines(path).joinToString("\n"))
    }
}