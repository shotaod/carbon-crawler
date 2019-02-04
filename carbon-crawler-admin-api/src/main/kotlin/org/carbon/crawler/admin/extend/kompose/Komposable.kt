package org.carbon.crawler.admin.extend.kompose

import io.ktor.http.HttpStatusCode
import org.carbon.crawler.admin.extend.ktor.ReservedException
import org.carbon.kompose.Komposable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.sql.SQLException

class PersistentErrorHandler<T> : Komposable<T>() {
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