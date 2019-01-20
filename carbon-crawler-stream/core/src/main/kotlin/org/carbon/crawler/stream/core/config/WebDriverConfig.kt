package org.carbon.crawler.stream.core.config

import org.carbon.cloud.config.spring.WebDriverProp
import org.carbon.crawler.stream.core.extend.selenium.DriverFactory
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.net.URL

/**
 * @author Soda 2018/08/11.
 */
@Import(WebDriverProp::class)
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
