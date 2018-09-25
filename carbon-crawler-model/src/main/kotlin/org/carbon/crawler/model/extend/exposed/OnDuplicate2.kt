package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.isAutoInc
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.math.BigInteger

/**
 * @author Soda 2018/08/17.
 */
class BatchInsertUpdateOnDuplicate2(table: Table, val onDupUpdate: List<Column<*>>) : BatchInsertStatement(table, false) {
    override fun prepareSQL(transaction: Transaction): String {
        val onUpdateSQL = if (onDupUpdate.isNotEmpty()) {
            " ON DUPLICATE KEY UPDATE " + onDupUpdate.joinToString { "${transaction.identity(it)}=VALUES(${transaction.identity(it)})" }
        } else ""
        return super.prepareSQL(transaction) + onUpdateSQL
    }
}

fun <T : Table, E> T.batchInsertOnDuplicateKeyUpdate(data: List<E>, onDuplicate: List<Column<*>>, body: BatchInsertUpdateOnDuplicate2.(E) -> Unit): List<Int> {
    return data.takeIf { it.isNotEmpty() }?.let {
        val insert = BatchInsertUpdateOnDuplicate2(this, onDuplicate)
        data.forEach {
            insert.addBatch()
            insert.body(it)
        }
        TransactionManager.current().exec(insert)
        columns.firstOrNull { it.columnType.isAutoInc }?.let { idCol ->
            insert.generatedKey?.mapNotNull {
                val value = it[idCol]
                when (value) {
                    is Long -> value.toInt()
                    is Int -> value
                    is BigInteger -> value.toInt()
                    null -> null
                    else -> error("can't find primary key of type Int or Long; map['$idCol']='$value'(${value::class}) (where map='$it')")
                }
            }
        }
    }.orEmpty()
}