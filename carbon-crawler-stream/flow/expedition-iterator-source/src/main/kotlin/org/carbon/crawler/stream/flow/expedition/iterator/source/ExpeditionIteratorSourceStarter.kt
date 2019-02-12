package org.carbon.crawler.stream.flow.expedition.iterator.source

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author Soda 2018/08/22.
 */
@SpringBootApplication
interface ExpeditionIteratorSourceStarter

fun main(args: Array<String>) {
    SpringApplication(ExpeditionIteratorSourceStarter::class.java)
        .run(*args)
}