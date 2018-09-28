package org.carbon.crawler.admin.aservice.dictionary

import org.carbon.crawler.admin.V1
import org.carbon.crawler.admin.aservice.JsonSuccessPage
import org.carbon.crawler.admin.aservice.JsonSuccessSingle
import org.carbon.crawler.admin.aservice.Page
import org.carbon.crawler.admin.aservice.PageRequest
import org.carbon.crawler.admin.aservice.ok
import org.carbon.crawler.admin.aservice.oks
import org.carbon.crawler.model.DictionaryEntity
import org.carbon.crawler.model.DictionaryTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Soda 2018/08/06.
 */
class DictionaryAppService {
    fun fetchDictionaries(pageRequest: PageRequest): JsonSuccessPage<DictionaryItem> = transaction {
        val (page, size) = pageRequest
        val entities = DictionaryTable.selectAll()
                .limit(size, offset = page)
                .map {
                    DictionaryItem(
                            it[DictionaryTable.id].value,
                            it[DictionaryTable.url],
                            it[DictionaryTable.title],
                            it[DictionaryTable.memo]
                    )
                }
        val count = DictionaryEntity.count()
        entities.oks(Page(page, count))
    }

    fun createDictionary(postRequest: V1.PostDictionary.Body): JsonSuccessSingle<Long> = transaction {
        DictionaryEntity.new {
            //            url = postRequest.url
//            title = postRequest.title
//            memo = postRequest.memo
        }.id.value.ok()
    }
}