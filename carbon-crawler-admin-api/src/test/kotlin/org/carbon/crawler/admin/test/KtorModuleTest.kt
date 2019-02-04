package org.carbon.crawler.admin.test

import org.junit.jupiter.api.BeforeEach

interface KtorModuleTest {
    @BeforeEach
    fun setUp() {
        System.setProperty("carbon.profile", "dev")
    }
}