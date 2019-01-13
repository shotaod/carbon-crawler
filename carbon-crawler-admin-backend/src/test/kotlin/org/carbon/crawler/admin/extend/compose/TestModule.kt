package org.carbon.crawler.admin.extend.compose

import io.ktor.application.Application
import io.ktor.server.testing.withTestApplication
import org.carbon.composer.Composable

class TestModule(private val module: Application.() -> Unit) : Composable<Unit>() {
    override fun invoke() {
        withTestApplication(module) {
            context.set(this)
            super.callChild()
        }
    }
}
