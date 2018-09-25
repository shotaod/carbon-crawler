package org.carbon.crawler.batch

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author Soda 2018/07/22.
 */
@SpringBootApplication
interface CarbonCrawlerStarter

fun main(args: Array<String>) {
    SpringApplication.run(CarbonCrawlerStarter::class.java, *args)
}
