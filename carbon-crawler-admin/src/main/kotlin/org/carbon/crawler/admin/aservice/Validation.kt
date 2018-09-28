package org.carbon.crawler.admin.aservice

import org.carbon.crawler.admin.V1

/**
 * @author Soda 2018/09/05.
 */
object GetDictionarySchema : Validate<V1.GetDictionary> {
    override val define: V1.GetDictionary.() -> Unit = {
        page should { it min 0 } or ("page" should "greater than or equal 0")
        size should { it min 0 } or ("size" should "greater than or equal 0")
    }
}

object PostDictionarySchema : Validate<V1.PostDictionary.Body> {
    override val define: V1.PostDictionary.Body.() -> Unit = {
        url should { it.url() } or ("url" should "correct format")
    }
}

interface Validate<T : Validate<T>> {
    val define: T.() -> Unit

    interface Should<T> {
        fun T.be(expected: T): Boolean = this == expected
    }

    class StringShould : Should<String> {
        private val urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
        fun String.url(): Boolean = urlRegex.matches(this)
    }

    class IntShould : Should<Int> {
        infix fun Int.range(range: IntRange): Boolean {
            val min = range.first
            val max = range.last
            if (min < 0 || max < 0)
                throw IllegalArgumentException("min and max should be greater than 0")

            if (min > max)
                throw IllegalArgumentException("min and max should be max > min")

            return this in min..max
        }

        infix fun Int.min(min: Int): Boolean {
            if (min < 0) throw IllegalArgumentException("min should be greater than 0")
            return this >= min
        }

        infix fun Int.max(max: Int): Boolean {
            if (max < 0) throw IllegalArgumentException("max should be greater than 0")
            return this >= max
        }
    }

    class Violation {
        infix fun with(message: String) {
        }
    }

    infix fun <T> T.should(block: Should<T>.(T) -> Boolean): XXX {
    }

    class XXX(val validators: MutableList<Any>) {
        private var _or: ErrorMessage? = null

        infix fun or(or: ErrorMessage) {
            _or = or
        }
    }

    data class ErrorMessage(val key: String, val verbose: String, val message: String)

    infix fun String.should(message: String): ErrorMessage = ErrorMessage(this, "should be", message)

    infix fun String.should(block: StringShould.(String) -> Boolean): XXX {
    }

    infix fun Int.should(block: IntShould.(Int) -> Boolean): XXX {
    }

    class Result {
        private val violations: MutableMap<String, String> = HashMap()
        fun add(violation: Pair<String, String>) {
            violations[violation.first] = violation.second
        }

        fun get(): Map<String, String> {
            return violations;
        }
    }

    class Matcher() {
        fun should(block: () -> Unit) {
        }
    }

    fun validate(): Any {
        item.validator()
    }
}