package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IdTable

/**
 * @author Soda 2018/07/28.
 */
interface TableSupport<T : Comparable<T>> {
    fun pk(value: T): EntityID<T>
}

class DefaultTableSupport<T : Comparable<T>>(private val table: IdTable<T>) : TableSupport<T> {
    override fun pk(value: T): EntityID<T> = EntityID(value, table)
}