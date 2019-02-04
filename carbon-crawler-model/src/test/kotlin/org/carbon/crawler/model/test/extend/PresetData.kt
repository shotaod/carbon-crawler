package org.carbon.crawler.model.test.extend

import org.carbon.crawler.model.extend.kompose.DBUtil
import org.carbon.kompose.Komposable
import org.dbunit.database.DatabaseConnection
import org.dbunit.operation.DatabaseOperation
import org.dbunit.util.fileloader.CsvDataFileLoader
import org.jetbrains.exposed.sql.Transaction

object PresetData : Komposable<Unit>() {
    override fun invoke() {

        with(context.context[DBUtil::class]) {
            clean()
        }
        with(context.context[Transaction::class]) {
            DatabaseOperation.CLEAN_INSERT.execute(
                DatabaseConnection(this.connection),
                CsvDataFileLoader().load("data")
            )
        }
        super.callChild()
    }
}