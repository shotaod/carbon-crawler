package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author Soda 2018/08/19.
 */
private val DATE_PATTERN = DateTimeFormatter.ofPattern("YYYY-MM-dd")
private val DATETIME_PATTERN = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSSSSS")

fun Table.localdatetime(name: String): Column<LocalDateTime> = registerColumn(name, MysqlLocalDateColumnType(true))

class MysqlLocalDateColumnType(private val time: Boolean) : ColumnType() {
    override fun sqlType(): String = if (time) "DATETIME" else "DATE"

    override fun nonNullValueToString(value: Any): String {
        if (value is String) return value
        val dateTime: LocalDateTime = when (value) {
            is LocalDate -> value.atTime(0, 0)
            is LocalDateTime -> value
            else -> error("Unexpected value: $value of ${value::class.qualifiedName}")
        }

        return if (time)
            "'${DATETIME_PATTERN.format(dateTime)}'"
        else
            "'${DATE_PATTERN.format(dateTime)}'"
    }

    override fun valueFromDB(value: Any): Any = when (value) {
        is LocalDate -> value
        is LocalDateTime -> value
        is Int -> LocalDateTime.ofInstant(Instant.ofEpochMilli(value.toLong()), ZoneId.systemDefault())
        is Long -> LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault())
        else -> DATETIME_PATTERN.parse(value.toString())
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is LocalDateTime) {
            return if (time) {
                java.sql.Timestamp.valueOf(value)
            } else {
                java.sql.Date.valueOf(value.toLocalDate())
            }
        }
        return value
    }
}