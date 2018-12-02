package org.carbon.crawler.admin

import org.junit.Before

interface KtorModuleTest {
    @Before
    fun setUp() {
        System.setProperty("config.resource", "application.test.conf")
    }
}