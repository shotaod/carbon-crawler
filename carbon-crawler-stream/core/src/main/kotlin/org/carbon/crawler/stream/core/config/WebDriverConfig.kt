package org.carbon.crawler.stream.core.config

import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.net.URL

/**
 * @author Soda 2018/08/11.
 */
@Import(DriverHubProp::class)
@Configuration
class WebDriverConfig(
        val prop: DriverHubProp
) {
    @Bean
    fun driverFactory() = DriverFactory(prop.toURL(), ChromeOptions())
}

@Configuration
@ConfigurationProperties("driver.hub")
class DriverHubProp {
    lateinit var url: String
    fun toURL(): URL = URL(url)
}