package org.carbon.crawler.admin.extend

import io.ktor.http.HttpStatusCode
import org.carbon.composer.Composable
import org.carbon.crawler.admin.extend.ktor.ReservedException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.sql.SQLException

class PersistentErrorHandler<T> : Composable<T>() {
    class DuplicateException : ReservedException(HttpStatusCode.BadRequest, "already exist")

    override fun invoke(): T =
        try {
            super.callChild()
        } catch (e: ExposedSQLException) {
            val original = e.cause as? SQLException ?: throw e
            when {
                original.errorCode == 1062 -> throw DuplicateException()
                else -> throw e
            }
        }
}