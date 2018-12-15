package org.carbon.crawler.admin.aservice

import io.ktor.http.HttpStatusCode
import org.carbon.crawler.model.extend.exposed.transactionL
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.sql.SQLException

abstract class Composable<T> {
    protected lateinit var composable: Composable<T>
    fun compose(composable: Composable<T>): Composable<T> {
        this.composable = composable
        return this
    }

    abstract fun invoke(): T
}

class ExpressionComposed<T>(private val expression: () -> T) : Composable<T>() {
    override fun invoke(): T = expression()
}

fun <T> compose(vararg handlers: Composable<T>, expression: () -> T): T =
        ExpressionComposed(expression)
                .let { handlers.toList() + it }
                .reduceRight { composed, acc -> composed.compose(acc) }
                .invoke()

class PersistentErrorHandler<T> : Composable<T>() {
    class DuplicateException : UseCaseException(HttpStatusCode.BadRequest, "already exist")

    override fun invoke(): T =
            try {
                super.composable.invoke()
            } catch (e: ExposedSQLException) {
                val original = e.cause as? SQLException ?: throw e
                when {
                    original.errorCode == 1062 -> throw DuplicateException()
                    else -> throw e
                }
            }
}

class TransactionLogging<T> : Composable<T>() {
    override fun invoke(): T = transactionL { super.composable.invoke() }
}