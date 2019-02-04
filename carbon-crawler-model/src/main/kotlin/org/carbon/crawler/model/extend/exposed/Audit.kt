package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityChange
import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Database
import java.time.LocalDateTime

/**
 * @author Soda 2018/08/16.
 */
abstract class AuditLongIdTable(name: String, columnName: String = "id") : LongIdTable(name, columnName) {
    val insertedAt = localdatetime("ins_at").default(LocalDateTime.now())
    val updatedAt = localdatetime("upd_at").nullable()
}

abstract class AuditLongEntity(id: EntityID<Long>, table: AuditLongIdTable) : LongEntity(id) {
    val insertedAt by table.insertedAt
    var updatedAt by table.updatedAt
}

private inline fun <reified T : Any> Any.mapTyped(): T? = this as? T

fun EntityChange.toEntity(): Entity<Long>? {
    val longEntityClass = entityClass.mapTyped<EntityClass<Long, Entity<Long>>>()
    val longId = id.mapTyped<EntityID<Long>>()

    if (longEntityClass == null || longId == null) return null

    return longEntityClass.findById(longId)
}

fun Database.enableAuditing(): Database {
    EntityHook.subscribe { action ->
        if (action.changeType == EntityChangeType.Updated) {
            action.toEntity()
                ?.mapTyped<AuditLongEntity>()
                ?.updatedAt = LocalDateTime.now()
        }
    }
    return this
}
