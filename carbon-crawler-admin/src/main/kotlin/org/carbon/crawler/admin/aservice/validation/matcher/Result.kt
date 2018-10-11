package org.carbon.crawler.admin.aservice.validation.matcher

import kotlin.reflect.KClass

/**
 * @author Soda 2018/10/08.
 */
// ===================================================================================
//                                                                          Result
//                                                                          ==========
sealed class MatcherResult {
    object Satisfy : MatcherResult() {
        override fun toString(): String {
            return "Satisfy()"
        }
    }

    data class Illegal<T : Any>(
            val type: KClass<T>,
            val group: ViolationGroup<T>,
            val message: String,
            val original: T
    ) : MatcherResult() {
        override fun toString(): String {
            return "Illegal(type=$type, group=$group, message='$message', original=$original)"
        }
    }
}

// ===================================================================================
//                                                                          Reasons
//                                                                          ==========
sealed class ViolationGroup<T : Any>(val type: KClass<T>, val code: String, val params: List<*> = emptyList<Any>()) {
    override fun toString(): String {
        return "ViolationGroup(type=${type.simpleName}, code='$code', params=$params)"
    }
}

typealias Reducer<T> = (illegals: List<MatcherResult.Illegal<T>>) -> MatcherResult

fun <T : Any> reducer(vector: Composition): Reducer<T> = { illegals ->
    val first = illegals.first()
    val groups = illegals.map { it.group }
    val compositeGroup = CompositeViolationGroup(first.type, groups, vector)
    MatcherResult.Illegal(first.type, compositeGroup, "composite violation", first.original)
}

sealed class Composition(val operator: String) {
    object OR : Composition("OR")
    object AND : Composition("AND")

    override fun toString(): String {
        return "Composition(operator='$operator')"
    }
}

class CompositeViolationGroup<T : Any>(
        _type: KClass<T>,
        compositions: List<ViolationGroup<T>>,
        vector: Composition
) : ViolationGroup<T>(_type, "Composite", listOf(vector) + compositions)

// -----------------------------------------------------
//                                               Basic
//                                               -------
sealed class BasicViolationGroup<T : Any>(_type: KClass<T>, _code: String, _params: List<*> = emptyList<Any>())
    : ViolationGroup<T>(_type, _code, _params) {
    companion object {
        inline fun <reified T : Any> Equal(a: T, b: T): BasicViolationGroup<T> =
                Equal(T::class, a, b)
    }

    class Equal<T : Any>(_type: KClass<T>, a: T, b: T) : BasicViolationGroup<T>(_type, "Equal", listOf(a, b))
}

// -----------------------------------------------------
//                                               String
//                                               -------
sealed class StringViolationGroup(_code: String, _params: List<*> = emptyList<Any>())
    : ViolationGroup<String>(String::class, _code, _params) {
    object Email : StringViolationGroup("Email")
    object URL : StringViolationGroup("URL")
    sealed class Length(_code: String, _params: List<Number>) : StringViolationGroup("Length.$_code", _params) {
        class Min(min: Int) : Length("Min", listOf(min))
        class MinEq(min: Int) : Length("MinEq", listOf(min))
        class Max(max: Int) : Length("Max", listOf(max))
        class MaxEq(max: Int) : Length("MaxEq", listOf(max))
    }
}

// -----------------------------------------------------
//                                               Number
//                                               -------
sealed class NumberViolationGroup(_code: String, _params: List<*> = emptyList<Any>())
    : ViolationGroup<Number>(Number::class, "Number.$_code", _params) {
    sealed class Length(_code: String) : NumberViolationGroup("Length.$_code") {
        class Range(val min: Number, val max: Number) : Length("Range")
        class Min(val min: Number) : Length("Min")
        class MinEq(val min: Number) : Length("MinEq")
        class Max(val max: Number) : Length("Max")
        class MaxEq(val max: Number) : Length("MaxEq")
    }
}
