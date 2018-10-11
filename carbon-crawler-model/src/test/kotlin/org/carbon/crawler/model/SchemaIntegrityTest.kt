package org.carbon.crawler.model

import org.carbon.crawler.model.extend.exposed.transactionL
import org.carbon.crawler.model.meta.Models
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.h2.jdbcx.JdbcDataSource
import org.hamcrest.CoreMatchers.equalTo
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.intLiteral
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.file.Paths

/**
 * @author Soda 2018/08/09.
 */
class SchemaIntegrityTest {
    private val schemaDirectory = "../carbon-crawler-model/schema/sql"

    @Before
    fun before() {
        cleanDatabase()
    }

    @Test
    fun test_schema_to_entity_class_integrity() {
        // -----------------------------------------------------
        //                                               given
        //                                               -------
        val dataSource = JdbcDataSource().apply {
            setURL("jdbc:h2:mem:MODE=MySQL;DB_CLOSE_DELAY=-1")
        }
        val flyway = Flyway().apply {
            this.dataSource = dataSource
            val currentDir = System.getProperty("user.dir")
            val location = Paths.get(Location.FILESYSTEM_PREFIX, currentDir).resolve(schemaDirectory).normalize().toString()
            println(location)
            setLocations(location)
        }

        Database.connect(dataSource)
        // -----------------------------------------------------
        //                                               when
        //                                               -------
        flyway.migrate()
        // -----------------------------------------------------
        //                                               then
        //                                               -------
        val tableCount = dataSource.connection.use {
            val res = it.prepareStatement("""
                | SELECT count(*)
                | FROM information_schema.tables
                | WHERE table_schema = 'PUBLIC'
            """.trimMargin()).executeQuery()
            res.next()
            res.getLong(1)
        }
        assertThat("table count is correct", tableCount - 1 /*flyway table*/, equalTo(Models.entities.size.toLong()))
        // assert no exceptions
        val noop = intLiteral(1) eq intLiteral(1)
        transactionL {
            Models.entities.forEach {
                it.find(noop).toList()
            }
        }
    }
}
