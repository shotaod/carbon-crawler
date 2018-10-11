package org.carbon.crawler.admin.aservice.validation

import org.carbon.crawler.admin.aservice.validation.matcher.IntMatcherScope
import org.carbon.crawler.admin.aservice.validation.matcher.MatcherResult
import org.carbon.crawler.admin.aservice.validation.matcher.StringMatcherScope

/**
 * @author Soda 2018/09/05.
 */
object Validator {
    fun <T : Validated<T>> validate(target: T): ValidationResult {
        val assert = Assert()
        target.def(assert, target)
        return when {
            assert.isValid() -> ObservanceResult(target)
            else -> ViolationResult(assert.violations)
        }
    }
}

class Assert {
    val violations = ViolationList()

    fun isValid(): Boolean = violations.isEmpty()

    infix fun String.should(block: StringMatcherScope.(String) -> MatcherResult) =
            call { block(StringMatcherScope, this) }

    infix fun Int.should(block: IntMatcherScope.(Int) -> MatcherResult) =
            call { block(IntMatcherScope, this) }

    private fun call(assertion: () -> MatcherResult): ViolationClause {
        val result = assertion()
        return when (result) {
            is MatcherResult.Satisfy -> NoopViolationClause()
            is MatcherResult.Illegal<*> -> SpecifyViolationClause(result, violations)
        }
    }
}
