package org.carbon.crawler.admin.aservice.validation.matcher

/**
 * @author Soda 2018/10/07.
 */
typealias Matcher<T> = T.() -> MatcherResult

private inline fun <reified T : Any> T.illegal(group: ViolationGroup<T>, message: String) =
        MatcherResult.Illegal(T::class, group, message, this)

interface MatcherScope<T> {
    infix fun T.be(matcher: Matcher<T>): MatcherResult = matcher(this)

    infix fun T.eq(other: T): MatcherResult

    // -----------------------------------------------------
    //                                               Logical
    //                                               -------
    fun or(vararg matches: MatcherResult): MatcherResult =
            if (MatcherResult.Satisfy in matches) MatcherResult.Satisfy
            else mergeIllegals(matches, Composition.OR)

    fun and(vararg matches: MatcherResult): MatcherResult =
            if (matches.all { it === MatcherResult.Satisfy }) MatcherResult.Satisfy
            else mergeIllegals(matches, Composition.AND)

    private fun mergeIllegals(matches: Array<out MatcherResult>, vector: Composition) =
            matches.filter { it !== MatcherResult.Satisfy }
                    .map {
                        @Suppress("UNCHECKED_CAST")
                        it as MatcherResult.Illegal<Any>
                    }
                    .let(reducer(vector))
}

object StringMatcherScope : MatcherScope<String> {
    private val urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
    private val emailRegex = "^[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+(\\.[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$".toRegex()

    // -----------------------------------------------------
    //                                               Infix
    //                                               -------
    override fun String.eq(other: String): MatcherResult =
            if (this == other) MatcherResult.Satisfy
            else this.illegal(BasicViolationGroup.Equal(this, other), "not match")

    infix fun String.minEq(min: Int): MatcherResult =
            if (this.length >= min) MatcherResult.Satisfy
            else this.illegal(StringViolationGroup.Length.MinEq(min), "character must be less than or equal to $min")

    infix fun String.min(min: Int): MatcherResult =
            if (this.length > min) MatcherResult.Satisfy
            else this.illegal(StringViolationGroup.Length.Min(min), "character must be less than $min")

    infix fun String.maxEq(max: Int): MatcherResult =
            if (this.length <= max) MatcherResult.Satisfy
            else this.illegal(StringViolationGroup.Length.MaxEq(max), "character must be less than or equal to $max")

    infix fun String.max(max: Int): MatcherResult =
            if (this.length < max) MatcherResult.Satisfy
            else this.illegal(StringViolationGroup.Length.Max(max), "character must be less than $max")

    // -----------------------------------------------------
    //                                               Shape
    //                                               -------
    val Email: Matcher<String> = { this.isEmail() }

    private fun String.isEmail(): MatcherResult =
            if (emailRegex.matches(this)) MatcherResult.Satisfy
            else this.illegal(StringViolationGroup.Email, "illegal email format")

    val URL: Matcher<String> = { this.isURL() }

    private fun String.isURL(): MatcherResult =
            if (urlRegex.matches(this)) MatcherResult.Satisfy
            else this.illegal(StringViolationGroup.URL, "illegal URL format")
}

object IntMatcherScope : MatcherScope<Int> {
    // -----------------------------------------------------
    //                                               Infix
    //                                               -------
    override fun Int.eq(other: Int): MatcherResult =
            if (this == other) MatcherResult.Satisfy
            else this.illegal(BasicViolationGroup.Equal(this, other), "not match")

    infix fun Int.range(range: IntRange): MatcherResult {
        val min = range.first
        val max = range.last
        if (min > max)
            throw IllegalArgumentException("min and max should be max > min")

        return if (this in min..max) MatcherResult.Satisfy
        else this.illegal(NumberViolationGroup.Length.Range(min, max), "number must be between $min and $max")
    }

    infix fun Int.min(min: Int): MatcherResult {
        if (min < 0) throw IllegalArgumentException("min should be greater than 0")
        return if (this >= min) MatcherResult.Satisfy
        else this.illegal(NumberViolationGroup.Length.Min(min), "number must be less than $min")
    }

    infix fun Int.max(max: Int): MatcherResult {
        if (max < 0) throw IllegalArgumentException("max should be greater than 0")
        return if (this <= max) MatcherResult.Satisfy
        else this.illegal(NumberViolationGroup.Length.Max(max), "number must be greater than $max")
    }
}
