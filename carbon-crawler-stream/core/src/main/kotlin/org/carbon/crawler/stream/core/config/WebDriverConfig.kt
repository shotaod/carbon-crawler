package org.carbon.crawler.stream.core.config

import org.carbon.cloud.config.spring.WebDriverProp
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URL

/**
 * @author Soda 2018/08/11.
 */
@EnableConfigurationProperties(WebDriverProp::class)
@Configuration
class WebDriverConfig(
    val p: WebDriverProp
) {
    @Bean
    fun driverFactory() = DriverFactory(
        URL("http://${p.host}:${p.port}/${p.path}"),
        ChromeOptions()
    )
}
