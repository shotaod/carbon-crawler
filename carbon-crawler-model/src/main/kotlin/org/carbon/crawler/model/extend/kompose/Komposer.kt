package org.carbon.crawler.model.extend.kompose

import org.carbon.crawler.model.extend.exposed.beginTransaction
import org.carbon.crawler.model.infra.meta.Meta
import org.carbon.kompose.Komposable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteAll
import org.slf4j.LoggerFactory

class Transaction<T>(private val logging: Boolean = false) : Komposable<T>() {
    override fun invoke(): T = beginTransaction(logging) { super.callChild() }
}

interface DBUtil {
    fun clean()
}

object RollbackTransaction : Komposable<Unit>() {
    private object RollbackException : Exception("for Rollback purpose")
    object DBUtilImpl : DBUtil {
        override fun clean() {
            SchemaUtils.create(*Meta.tablesByAncestor)
            Meta.tablesByAncestor
                .reversed()
                .forEach { it.deleteAll() }
        }
    }

    private val log = LoggerFactory.getLogger(RollbackTransaction::class.java)
    override fun invoke() {
        context.setAs(DBUtilImpl, DBUtil::class)
        try {
            beginTransaction {
                context.setAs(this, Transaction::class)
                super.callChild()
                throw RollbackException
            }
        } catch (ex: RollbackException) {
            log.info("rollback! catch rollback-purpose exception")
        }
    }
}