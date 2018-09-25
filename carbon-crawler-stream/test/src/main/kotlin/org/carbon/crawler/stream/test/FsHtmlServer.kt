package org.carbon.crawler.stream.test

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND
import fi.iki.elonen.NanoHTTPD.Response.Status.OK
import fi.iki.elonen.util.ServerRunner
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * convenient entry point
 * @author Soda 2018/08/03.
 */
fun main(args: Array<String>) {
    val server = FsHtmlServer(8080, "/pages/carbon_wiki")
    ServerRunner.executeInstance(server)
}

class FsHtmlServer(port: Int, root: String) : NanoHTTPD(port) {
    private val log = LoggerFactory.getLogger(FsHtmlServer::class.java)
    private val basePath = FsHtmlServer::class.java.getResource(root).path

    override fun serve(session: IHTTPSession): Response {
        val path = Paths.get(basePath, session.uri)
        log.info("receive: ${session.uri} ---> search html: $path")
        if (!Files.exists(path)) return newFixedLengthResponse(NOT_FOUND, "text/plain", "not found")
        val bytes = Files.readAllBytes(path)
        val byteData = ByteArrayInputStream(bytes)
        return newFixedLengthResponse(OK, "text/html", byteData, bytes.size.toLong())
    }
}