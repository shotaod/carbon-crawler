package org.carbon.crawler.admin

import org.junit.jupiter.api.BeforeEach

interface KtorModuleTest {
    @BeforeEach
    fun setUp() {
        System.setProperty("carbon.profile", "test")
    }
}