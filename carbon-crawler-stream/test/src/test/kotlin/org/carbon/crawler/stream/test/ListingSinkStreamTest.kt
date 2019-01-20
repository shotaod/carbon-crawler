package org.carbon.crawler.stream.test

import org.carbon.crawler.stream.test.extend.AbstractStreamTests
import org.carbon.crawler.stream.test.extend.StreamDefinition
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test


class ListingSinkStreamTest : AbstractStreamTests() {

    @Test
    fun deploy() {
        val stream = StreamDefinition(
            "crawl-stream",
            "crawl-listing-source | crawl-listing-sink",
            mapOf(
                "app.crawl-listing-source.trigger.fixedDelay" to "1",
                "app.crawl-listing-source.trigger.initialDelay" to "0",
                "app.crawl-listing-source.trigger.maxMessage" to "100",
                "app.crawl-listing-source.trigger.timeUnit" to "SECONDS"
            ))

        deployStream(stream)
        assertTrue("Source not started", waitForLogEntry(stream["crawl-listing-source"]!!, "crawl-listing-source"))
        assertTrue("Sink not started", waitForLogEntry(stream["crawl-listing-sink"]!!, "crawl-listing-sink"))
    }
}