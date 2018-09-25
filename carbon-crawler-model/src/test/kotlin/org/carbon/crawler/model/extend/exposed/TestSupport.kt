package org.carbon.crawler.model.extend.exposed

import org.h2.jdbcx.JdbcDataSource
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntityClass
import javax.sql.DataSource

/**
 * @author Soda 2018/08/17.
 */
fun setupDataSource(): DataSource = JdbcDataSource().apply {
    setURL("jdbc:h2:mem:MODE=MySQL;DB_CLOSE_DELAY=-1")
}

class TestEntity(id: EntityID<Long>) : AuditLongEntity(id, TestTable) {
    companion object : LongEntityClass<TestEntity>(TestTable), TableSupport<Long> by DefaultTableSupport(TestTable)

    var memo by TestTable.memo
    var uniqueKey by TestTable.uniqueKey
}

object TestTable : AuditLongIdTable(name = "test") {
    val memo = varchar("memo", 1023)
    val uniqueKey = varchar("unique_key", 127).uniqueIndex()
}
