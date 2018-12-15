package org.carbon.crawler.admin.extend.carbon.validation

import org.carbon.objects.validation.BeCounterExpression
import org.carbon.objects.validation.matcher.max
import org.carbon.objects.validation.matcher.min

val NumberMin: (i: Int) -> BeCounterExpression<Int> = { i -> { this min i } }
val NumberMax: (i: Int) -> BeCounterExpression<Int> = { i -> { this max i } }
