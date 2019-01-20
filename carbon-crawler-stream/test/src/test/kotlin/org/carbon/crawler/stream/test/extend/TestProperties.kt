package org.carbon.crawler.stream.test.extend

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties
class TestProperties {
    var maxWaitTime: String = "120"
    var deployPauseRetries: String = "30"
    var deployPauseTime: String = "5"
    lateinit var serverUri: String
    lateinit var streamRegistrationResource: String
}