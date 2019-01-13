package org.carbon.crawler.admin.infra

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.withClue
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.ktor.application.Application
import org.carbon.composer.compose
import org.carbon.crawler.admin.KtorModuleTest
import org.carbon.crawler.admin.TestModule
import org.carbon.crawler.admin.module
import org.carbon.crawler.model.domain.HostRepository
import org.carbon.crawler.model.extend.composer.DBUtil
import org.carbon.crawler.model.extend.composer.RollbackTransaction
import org.carbon.crawler.model.infra.record.CrawlDetailQueryTable
import org.carbon.crawler.model.infra.record.CrawlListQueryTable
import org.carbon.crawler.model.infra.record.HostTable
import org.carbon.crawler.model.infra.record.PageAttributeTable
import org.carbon.crawler.model.infra.record.PageTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.junit.jupiter.api.Test


class HostRepositoryTest : KtorModuleTest {

    @Test
    fun test_fetch() = compose(TestModule(Application::module), RollbackTransaction) {
        with(context[DBUtil::class]) {
            clean()
        }

        val host1Id = HostTable.insertAndGetId {
            it[url] = "https://carbon.org"
            it[title] = "carbon.org"
            it[memo] = "this is carbon web site"
        }.also { hId ->
            CrawlListQueryTable.insert {
                it[hostId] = hId
                it[listingPagePath] = "page/carat/list"
                it[listingLinkQuery] = "xpath://div/section/ul/li/a"
            }
        }

        val host2Id = HostTable.insertAndGetId {
            it[url] = "https://example.com"
            it[title] = "example.com"
        }.also { hId ->
            PageTable.insert {
                it[hostId] = hId
                it[title] = "page1"
                it[url] = "page1"
            }
            PageTable.insertAndGetId { it ->
                it[hostId] = hId
                it[title] = "page2"
                it[url] = "page2"
            }.let { pId ->
                PageAttributeTable.insert {
                    it[pageId] = pId
                    it[key] = "hoge"
                    it[value] = "fuga"
                    it[type] = "text/text"
                }
            }
            CrawlListQueryTable.insert {
                it[hostId] = hId
                it[listingPagePath] = "page/list"
                it[listingLinkQuery] = "xpath://a"
            }
            CrawlDetailQueryTable.insert {
                it[hostId] = hId
                it[name] = "for title"
                it[query] = "xpath://h2"
                it[type] = "text/text"
            }
            CrawlDetailQueryTable.insert {
                it[hostId] = hId
                it[name] = "for content"
                it[query] = "xpath://section"
                it[type] = "text/text"
            }
        }
        val host = HostRepository.fetch(0, 2)
        withClue("size 2 return 2 records") { host.size shouldBe 2 };

        {
            val (id, url, title, memo, data, query) = host[0]
            id shouldBe host1Id.value
            url shouldBe "https://carbon.org"
            title shouldBe "carbon.org"
            memo shouldBe "this is carbon web site"
            data should beEmpty()
            // todo add query assertion
        }();

        {
            val (id, url, title, memo, data, query) = host[1]
            id shouldBe host2Id.value
            url shouldBe "https://example.com"
            title shouldBe "example.com"
            memo shouldBe null
            data should haveSize(2)
            // todo add query assertion
        }()
    }
}