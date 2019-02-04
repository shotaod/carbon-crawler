package org.carbon.crawler.stream.integration.extend.spring.dataflow

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("carbon.test.integ")
class TestProperties {
    var maxWaitTime: String = "120"
    var deployPauseRetries: String = "30"
    var pauseTime: String = "5"
    lateinit var deployerUri: String
    lateinit var defFile: String
}
