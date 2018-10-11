package org.carbon.crawler.admin.aservice.validation

/**
 * @author Soda 2018/10/07.
 */
typealias Definition<T> = Assert.(T) -> Unit

interface Validated<T : Validated<T>> {
    val def: Definition<T>
}
