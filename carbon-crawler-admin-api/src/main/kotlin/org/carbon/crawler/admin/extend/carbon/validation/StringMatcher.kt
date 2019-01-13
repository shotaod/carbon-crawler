package org.carbon.crawler.admin.extend.carbon.validation

import org.carbon.objects.validation.BeCounterExpression
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.source.Code
import org.carbon.objects.validation.evaluation.source.ParamList
import org.carbon.objects.validation.matcher.max
import org.carbon.objects.validation.matcher.min
import org.carbon.objects.validation.matcher.reject


val LengthMin: (i: Int) -> BeCounterExpression<String> = { i -> { this min i } }
val LengthMax: (i: Int) -> BeCounterExpression<String> = { i -> { this max i } }
val NumberString: BeCounterExpression<String> = { this.isNumberString() }
val XPath: BeCounterExpression<String> = { this.isXpath() }
@Suppress("FunctionName")
fun OneOf(vararg value: String): BeCounterExpression<String> = { this.oneOf(*value) }

fun String.isNumberString(): Evaluation = this.toIntOrNull()
    ?.let { Evaluation.Accepted }
    ?: this.reject(
        Code("String.NumberString"),
        ParamList(emptyList<String>()),
        "Not a number"
    )

val xPathReg = "xpath://.+".toRegex()
fun String.isXpath(): Evaluation =
    if (xPathReg.matches(this)) Evaluation.Accepted
    else this.reject(
        Code("String.XPath"),
        ParamList(emptyList<String>()),
        "Not a valid xpath"
    )

fun String.oneOf(vararg value: String): Evaluation =
    if (value.any { this === it }) Evaluation.Accepted
    else this.reject(
        Code("String.OneOf"),
        ParamList(value.toList()),
        "Not match"
    )
