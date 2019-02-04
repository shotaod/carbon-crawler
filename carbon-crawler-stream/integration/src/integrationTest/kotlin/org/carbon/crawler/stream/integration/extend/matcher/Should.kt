package org.carbon.crawler.stream.integration.extend.matcher

import org.opentest4j.AssertionFailedError

fun <T> Collection<T>.shouldBeFound(predicate: (T) -> Boolean, assertion: (T) -> Unit): T = this.find(predicate)
    ?.also(assertion)
    ?: throw AssertionFailedError("not found element")
