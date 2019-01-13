package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.StatementInterceptor
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.PreparedStatement

/**
 * @author Soda 2018/08/05.
 */
private object Logger : SqlLogger {
    private val logger = LoggerFactory.getLogger("org.carbon.sql.logger")
    override fun log(context: StatementContext, transaction: Transaction) = logger.info(context.expandArgs(transaction))
    fun log(message: String) = logger.info(message)
}

private object SqlLoggingInterceptor : StatementInterceptor {
    override fun afterExecution(transaction: Transaction, contexts: List<StatementContext>, executedStatement: PreparedStatement) {
        val statement = contexts.first().statement
            as? BatchInsertStatement
            ?: return contexts.forEach { Logger.log(it, transaction) }

        val sql = statement.prepareSQL(transaction)
        val sqlWithValues = statement.arguments().joinToString(separator = "\n  ", prefix = "$sql\n  ") { arg ->
            arg.joinToString(separator = ",", prefix = "(", postfix = ")") { (_, value) ->
                value.toString()
            }
        }
        Logger.log(sqlWithValues)
    }
}

fun <T> transactionL(statement: Transaction.() -> T): T {
    val wrapStatement: Transaction.() -> T = {
        registerInterceptor(SqlLoggingInterceptor)
        statement()
    }
    return transaction(statement = wrapStatement)
}