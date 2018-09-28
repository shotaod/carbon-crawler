package org.carbon.crawler.admin.aservice

/**
 * @author Soda 2018/08/06.
 */
data class JsonSuccessSingle<T>(
        val item: T
)

data class JsonSuccessList<T>(
        val items: List<T>
)

data class Page(
        val index: Int,
        val max: Int
)

data class JsonSuccessPage<T>(
        val items: List<T>,
        val page: Page
)

fun <T> T.ok(): JsonSuccessSingle<T> = JsonSuccessSingle(this)

fun <T> List<T>.oks(): JsonSuccessList<T> = JsonSuccessList(this)

fun <T> List<T>.oks(page: Page): JsonSuccessPage<T> = JsonSuccessPage(this, page)