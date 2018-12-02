package org.carbon.crawler.admin.aservice.dictionary

import io.kotlintest.tables.row
import io.ktor.application.Application
import io.ktor.server.testing.withTestApplication
import org.carbon.crawler.admin.KtorModuleTest
import org.carbon.crawler.admin.module
import org.carbon.crawler.admin.www.v1.dictionary.GetDictionaryParameter
import org.carbon.crawler.model.DictionaryTable
import org.carbon.crawler.model.extend.exposed.transactionL
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.batchInsert
import org.junit.Test

class DictionaryAppServiceTest : KtorModuleTest {

    @Test
    fun fetchDictionaries(): Unit = withTestApplication(Application::module) {
        // Given:
        val data = listOf(
                //row(url, title, memo)
                row("https://www.tdd.com", "tdd.com", "this is tdd.com"),
                row("https://www.bdd.com", "bdd.com", "this is bdd.com"),
                row("https://www.atdd.com", "atdd.com", "this is atdd.com")
        )
        transactionL {
            create(DictionaryTable)

            DictionaryTable.batchInsert(data) {
                this[DictionaryTable.url] = it.a
                this[DictionaryTable.title] = it.b
                this[DictionaryTable.memo] = it.c
            }
        }

        // When:
        val appService = DictionaryAppService()
        appService.fetchDictionaries(GetDictionaryParameter.get("0", "10"))
    }
}