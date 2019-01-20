package org.carbon.crawler.stream.flow.crawl.listing.source

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author Soda 2018/08/22.
 */
@SpringBootApplication
interface CrawlListingSourceStarter

fun main(args: Array<String>) {
    SpringApplication(CrawlListingSourceStarter::class.java)
        .apply {
            webApplicationType = WebApplicationType.NONE
        }
        .run(*args)
}