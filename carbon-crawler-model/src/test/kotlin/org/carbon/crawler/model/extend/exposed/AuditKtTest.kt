package org.carbon.crawler.model.extend.exposed

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.junit.Test

/**
 * @author Soda 2018/08/16.
 */
class AuditKtTest {
    @Test
    fun test_EnableAuditing_create() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        val dataSource = setupDataSource()
        Database.connect(dataSource)
                .enableAuditing()

        transactionL {
            create(TestTable)
        }
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        val createdEntity = transactionL {
            TestEntity.new {
                memo = "this record is created by test"
            }
        }
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        assertThat("", createdEntity.insertedAt, notNullValue())
    }

    @Test
    fun test_EnableAuditing_update() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        val dataSource = setupDataSource()
        Database.connect(dataSource)
                .enableAuditing()

        transactionL {
            create(TestTable)
        }
        val createdEntity = transactionL {
            TestEntity.new {
                memo = "this record is created by test"
            }
        }
        val insertedAt = createdEntity.insertedAt
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        transactionL {
            createdEntity.memo = "this record is update by test"
        }
        val updatedRecord = transactionL {
            TestEntity.findById(createdEntity.id)
        }
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        assertThat("updated entity remain insertedAt ", updatedRecord!!.insertedAt, equalTo(insertedAt))
        assertThat("updated entity fulfill updatedAt", updatedRecord.updatedAt, notNullValue())
    }
}
