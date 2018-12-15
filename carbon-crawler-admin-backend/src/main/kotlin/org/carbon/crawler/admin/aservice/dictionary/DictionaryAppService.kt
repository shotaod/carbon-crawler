package org.carbon.crawler.admin.aservice.dictionary

import org.carbon.crawler.admin.aservice.Pager
import org.carbon.crawler.admin.aservice.PersistentErrorHandler
import org.carbon.crawler.admin.aservice.TransactionLogging
import org.carbon.crawler.admin.aservice.by
import org.carbon.crawler.admin.aservice.compose
import org.carbon.crawler.admin.aservice.toItem
import org.carbon.crawler.admin.www.v1.dictionary.GetDictionaryParameter
import org.carbon.crawler.admin.www.v1.dictionary.PostDictionaryBody
import org.carbon.crawler.model.DictionaryEntity
import org.carbon.crawler.model.DictionaryTable
import org.jetbrains.exposed.sql.selectAll

/**
 * @author Soda 2018/08/06.
 */
class DictionaryAppService {
    fun fetchDictionaries(param: GetDictionaryParameter) = compose(TransactionLogging()) {
        val (page, size) = param
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
        entities by Pager(page, count)
    }

    fun saveDictionary(body: PostDictionaryBody) = compose(
            PersistentErrorHandler(),
            TransactionLogging()) {
        val entity = DictionaryEntity.new {
            url = body.url
            title = body.title
            memo = body.memo
        }
        entity.id.value.toItem()
    }
}
