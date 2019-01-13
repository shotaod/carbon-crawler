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
open class BatchInsertUpdateOnDuplicate<V : Any>(
    table: Table,
    private val body: BatchInsertUpdateOnDuplicate<V>.(V) -> Unit,
    private val onDuplicateBody: BatchInsertUpdateOnDuplicate<V>.(V) -> Unit
) : BatchInsertStatement(table, false) {
    private var onDuplicateData: MutableList<List<Pair<Column<*>, Any?>>> = mutableListOf()
    private var enableOnDuplicate: Boolean = true
    override var arguments: List<List<Pair<Column<*>, Any?>>>? = null
        get() = field ?: run {
            val fieldValue = onDuplicateData
                .takeIf { enableOnDuplicate }
                ?.toList()
                ?.let { onDuplicateData ->
                    super.arguments
                        ?.mapIndexed { i, data -> data + onDuplicateData[i] }
                }
                ?: super.arguments
            field = fieldValue
            return fieldValue
        }


    fun handleData(seeds: List<V>) {
        seeds.forEach { seed ->
            // ______________________________________________________
            //
            // @ apply insert value statement
            addBatch()
            body(seed)
            // ______________________________________________________
            //
            // @ apply update on duplicate statement
            data[data.lastIndex] = LinkedHashMap(values)
            values.clear()
            data.add(values)
            onDuplicateBody(seed)
            onDuplicateData.add(data.removeAt(data.lastIndex).toList())
            values.clear()
            values.putAll(data.last())
        }
    }

    private fun <R> ignoreOnDuplicate(body: () -> R): R {
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
        val onDuplicateStatement = onDuplicateData
            .first()
            .joinToString(
                separator = ", ",
                prefix = " ON DUPLICATE KEY UPDATE "
            ) { (col, value) ->
                "${transaction.identity(col)} = ${queryBuilder.registerArgument(col, value)}"
            }
        return baseSql + onDuplicateStatement
    }
}

fun <T : Table, V : Any> T.batchInsertV2(
    data: Iterable<V>,
    ignore: Boolean = false,
    onDuplicate: (BatchInsertUpdateOnDuplicate<V>.(V) -> Unit)? = null,
    body: BatchInsertStatement.(V) -> Unit): List<Long> {
    return columns.firstOrNull { it.columnType.isAutoInc }
        ?.let { idCol ->
            if (onDuplicate == null) batchInsert(data, ignore, body).mapNotNull { (it[idCol] as? Number)?.toLong() }
            else batchInsertOnDuplicateKeyUpdate(data.toList(), body, onDuplicate)
        }.orEmpty()
}

private fun <T : Table, V : Any> T.batchInsertOnDuplicateKeyUpdate(
    data: List<V>,
    body: BatchInsertUpdateOnDuplicate<V>.(V) -> Unit,
    onDuplicate: BatchInsertUpdateOnDuplicate<V>.(V) -> Unit): List<Long> =
    data.takeIf { it.isNotEmpty() }
        ?.let {
            val insert = BatchInsertUpdateOnDuplicate(this, body, onDuplicate)
                .apply { handleData(data) }

            TransactionManager.current().exec(insert)
            columns.firstOrNull { it.columnType.isAutoInc }?.let { idCol ->
                insert.generatedKey?.mapNotNull {
                    val value = it[idCol]
                    when (value) {
                        is Number -> value.toLong()
                        null -> null
                        else -> error("can't find primary key of type Int or Long; map['$idCol']='$value as ${value::class}' (where map='$it')")
                    }
                }
            }
        }.orEmpty()
