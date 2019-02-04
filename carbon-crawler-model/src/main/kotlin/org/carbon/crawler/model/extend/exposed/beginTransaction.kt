package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.StatementInterceptor
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.PreparedStatement

private object SqlLoggingInterceptor : StatementInterceptor {
    private val logger = LoggerFactory.getLogger(SqlLoggingInterceptor::class.java)
    override fun afterExecution(transaction: Transaction, contexts: List<StatementContext>, executedStatement: PreparedStatement) {
        val batchStatement = contexts.first().statement
            as? BatchInsertStatement
            ?: return contexts.forEach { logger.info(it.expandArgs(transaction)) }

        val sql = batchStatement.prepareSQL(transaction)
        val sqlWithValues = batchStatement.arguments().joinToString(separator = "\n  ", prefix = "$sql\n  ") { arg ->
            arg.joinToString(separator = ",", prefix = "(", postfix = ")") { (_, value) ->
                value.toString()
            }
        }
        logger.info(sqlWithValues)
    }
}

fun <T> beginTransaction(logging: Boolean = true, statement: Transaction.() -> T): T {
    val wrapStatement: Transaction.() -> T = {
        if (logging) registerInterceptor(SqlLoggingInterceptor)
        statement()
    }
    return transaction(statement = wrapStatement)
}