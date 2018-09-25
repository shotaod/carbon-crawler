package org.carbon.crawler.stream.flow.crawl.order.source

import org.carbon.crawler.model.CrawlSourceTable
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author Soda 2018/08/22.
 */
@SpringBootApplication
interface CrawlOrderSourceStarter

fun main(args: Array<String>) {
    println(CrawlSourceTable)
    SpringApplication(CrawlOrderSourceStarter::class.java)
            .also {
                it.webApplicationType = WebApplicationType.NONE
            }
            .run(*args)
}