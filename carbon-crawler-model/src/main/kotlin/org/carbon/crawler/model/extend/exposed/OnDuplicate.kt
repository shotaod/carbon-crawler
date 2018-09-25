package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.isAutoInc
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

/**
 * @author Soda 2018/08/16.
 */
open class BatchInsertUpdateOnDuplicate(table: Table) : BatchInsertStatement(table, false) {
    protected var onDupData: MutableMap<Column<*>, Any?>? = null
    protected var enableOnDuplicate: Boolean = true
    override var arguments: List<List<Pair<Column<*>, Any?>>>? = null
        get() = field ?: run {
            val fieldValue = onDupData
                    ?.takeIf { enableOnDuplicate }
                    ?.toList()
                    ?.let { dups ->
                        super.arguments
                                ?.firstOrNull()
                                ?.plus(dups)
                                ?.let { listOf(it) }
                    }
                    ?: super.arguments
            field = fieldValue
            return fieldValue
        }

    open fun applyDupData() {
        values.clear()
        arguments = null
        onDupData = data.removeAt(data.lastIndex)
    }

    fun <R> ignoreOnDuplicate(body: () -> R): R {
        enableOnDuplicate = false
        arguments = null
        val r = body()

        enableOnDuplicate = true
        arguments = null

        return r
    }

    override fun prepareSQL(transaction: Transaction): String {
        val baseSql = ignoreOnDuplicate {
            super.prepareSQL(transaction)
        }
        val queryBuilder = QueryBuilder(true)
        val onDuplicateStatement = onDupData
                ?.toList()
                ?.joinToString(prefix = " ON DUPLICATE KEY UPDATE ") { (col, value) ->
                    "${transaction.identity(col)} = ${queryBuilder.registerArgument(col, value)}"
                } ?: ""
        return baseSql + onDuplicateStatement
    }
}

fun <T : Table, E : Any> T.batchInsert(
        data: Iterable<E>,
        ignore: Boolean = false,
        onDuplicate: (BatchInsertUpdateOnDuplicate.() -> Unit)? = null,
        body: BatchInsertStatement.(E) -> Unit): List<Long> {
    return columns.firstOrNull { it.columnType.isAutoInc }
            ?.let { idCol ->
                if (onDuplicate == null) batchInsert(data, ignore, body).mapNotNull { it[idCol] as? Long }
                else batchInsertOnDuplicateKeyUpdate(data.toList(), body, onDuplicate)
            }.orEmpty()
}

private fun <T : Table, E : Any> T.batchInsertOnDuplicateKeyUpdate(data: List<E>, body: BatchInsertUpdateOnDuplicate.(E) -> Unit, onDuplicate: BatchInsertUpdateOnDuplicate.() -> Unit): List<Long> =
        data.takeIf { it.isNotEmpty() }
                ?.let {
                    val insert = BatchInsertUpdateOnDuplicate(this)
                    data.forEach {
                        insert.addBatch()
                        insert.body(it)
                    }
                    insert.applyDupData()
                    insert.onDuplicate()

                    TransactionManager.current().exec(insert)
                    columns.firstOrNull { it.columnType.isAutoInc }?.let { idCol ->
                        insert.generatedKey?.mapNotNull {
                            val value = it[idCol]
                            when (value) {
                                is Long -> value
                                is Int -> value.toLong()
                                null -> null
                                else -> error("can't find primary key of type Int or Long; map['$idCol']='$value' (where map='$it')")
                            }
                        }
                    }
                }.orEmpty()
