package org.carbon.crawler.test.lib.httpd

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND
import fi.iki.elonen.NanoHTTPD.Response.Status.OK
import fi.iki.elonen.util.ServerRunner
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 *  HTML hosting server backed by File System
 *
 * @author Soda 2018/08/03.
 */
/**
 * convenience entry point
 */
fun main(args: Array<String>) {
    val server = FsHtmlServer(8111, "/html")
    ServerRunner.executeInstance(server)
}

class FsHtmlServer(port: Int, root: String) : NanoHTTPD(port) {
    private val basePath = FsHtmlServer::class.java.getResource(root).path

    override fun serve(session: IHTTPSession): Response {
        val path = Paths.get(basePath, session.uri)
        if (!Files.exists(path)) return newFixedLengthResponse(NOT_FOUND, "text/plain", "not found")
        val bytes = Files.readAllBytes(path)
        val byteData = ByteArrayInputStream(bytes)
        return newFixedLengthResponse(OK, "text/html", byteData, bytes.size.toLong())
    }
}
