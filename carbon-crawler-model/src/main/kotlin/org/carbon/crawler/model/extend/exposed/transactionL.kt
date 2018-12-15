package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

/**
 * @author Soda 2018/08/05.
 */
internal val carbonLogger = object : SqlLogger {
    val logger = LoggerFactory.getLogger("org.carbon.sql.logger")
    override fun log(context: StatementContext, transaction: Transaction) = logger.info(context.expandArgs(transaction))
}

fun <T> transactionL(statement: Transaction.() -> T): T {
    val wrapStatement: Transaction.() -> T = {
        addLogger(carbonLogger)
        statement()
    }
    return transaction(statement = wrapStatement)
}