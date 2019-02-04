package org.carbon.crawler.admin.test.extend.kompose

import io.ktor.application.Application
import io.ktor.server.testing.withTestApplication
import org.carbon.kompose.Komposable

class TestModule(private val module: Application.() -> Unit) : Komposable<Unit>() {
    override fun invoke() {
        withTestApplication(module) {
            context.set(this)
            super.callChild()
        }
    }
}
