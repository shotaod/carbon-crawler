package org.carbon.crawler.stream.core.extend.carbon

import org.carbon.kompose.Komposable
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class ExceptionLogging<OUT>(klass: KClass<*>) : Komposable<OUT>() {
    private val logger = LoggerFactory.getLogger(klass.java)

    override fun invoke(): OUT = try {
        super.callChild()
    } catch (e: Exception) {
        logger.warn("handled exception", e)
        throw e
    }
}