package org.carbon.crawler.admin.aservice.validation

import org.carbon.crawler.admin.aservice.validation.matcher.ViolationGroup
import kotlin.reflect.KClass

/**
 * @author Soda 2018/10/07.
 */
sealed class ValidationResult

data class ObservanceResult<T>(val data: T) : ValidationResult()
data class ViolationResult(val violations: ViolationList) : ValidationResult()

data class ViolationKey(
        val keys: List<String>,
        val index: Int? = null
)

data class Violation<T : Any>(
        val message: String,
        val key: ViolationKey?,
        val type: KClass<T>,
        val group: ViolationGroup<T>
) {
    override fun toString(): String {
        return "Violation(message='$message', key=$key, type=$type, group=$group)"
    }
}

class ViolationList : Iterable<Violation<*>> {
    private val item: MutableList<Violation<*>> = mutableListOf()

    fun add(violation: Violation<*>) = item.add(violation)
    fun isEmpty(): Boolean = item.isEmpty()

    override fun iterator(): Iterator<Violation<*>> = item.iterator()
}