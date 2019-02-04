package org.carbon.crawler.scdf

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.cloud.dataflow.server.EnableDataFlowServer

@EnableDataFlowServer
@SpringBootApplication(exclude = [
    UserDetailsServiceAutoConfiguration::class
])
interface Starter

fun main(args: Array<String>) {
    SpringApplication(Starter::class.java)
        .run(*args)
}
