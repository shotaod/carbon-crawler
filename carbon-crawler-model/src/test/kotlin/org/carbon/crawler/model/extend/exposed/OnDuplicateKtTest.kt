package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import java.time.LocalDateTime

/**
 * @author Soda 2018/08/17.
 */
class OnDuplicateKtTest {
    //@Test
    fun test_batchInsert() {
        val dataSource = setupDataSource()
        Database.connect(dataSource)
        transactionL {
            create(TestTable)
        }
        transactionL {
            TestTable.batchInsert(listOf("hoge", "piyo"),
                    onDuplicate = {
                        this[TestTable.updatedAt] = LocalDateTime.now()
                    },
                    body = {
                        this[TestTable.memo] = it
                        this[TestTable.uniqueKey] = it
                    })
        }
    }
}