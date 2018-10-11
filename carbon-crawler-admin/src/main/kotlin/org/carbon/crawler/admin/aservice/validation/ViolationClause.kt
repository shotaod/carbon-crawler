package org.carbon.crawler.admin.aservice.validation

import org.carbon.crawler.admin.aservice.validation.matcher.MatcherResult

/**
 * @author Soda 2018/10/07.
 */
// ===================================================================================
//                                                                     ViolationClause
//                                                                          ==========
sealed class ViolationClause {
    abstract infix fun otherwise(specify: Specify)
}

class NoopViolationClause : ViolationClause() {
    override fun otherwise(specify: Specify) = Unit
}

class SpecifyViolationClause<T : Any>(
        private val illegal: MatcherResult.Illegal<T>,
        private val list: ViolationList) : ViolationClause() {
    override fun otherwise(specify: Specify) {
        specify.toViolation(illegal).also { list.add(it) }
    }
}

// ===================================================================================
//                                                                          Specify
//                                                                          ==========
class Specify(
        private vararg val key: String,
        private val at: Int? = null,
        private val message: String? = null
) {
    fun <T : Any> toViolation(illegal: MatcherResult.Illegal<T>): Violation<T> = Violation(
            if (message !== null) message else illegal.message,
            ViolationKey(key.toList(), at),
            illegal.type,
            illegal.group
    )
}
