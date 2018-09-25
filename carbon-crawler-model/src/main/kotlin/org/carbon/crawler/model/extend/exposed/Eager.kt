package org.carbon.crawler.model.extend.exposed

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.Referrers
import org.jetbrains.exposed.sql.transactions.TransactionManager
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Trait for EAGER fetch strategy
 *
 * @author Soda 2018/08/02.
 */
interface EagerTrait {
    class EagerReferrers<ID : Comparable<ID>, Parent : Entity<ID>, Child : Entity<ID>>(ref: Referrers<ID, Parent, ID, Child>, entity: Parent) {
        private val cachedReferrers = if (ref.cache) ref else Referrers(ref.reference, ref.factory, true)
        private var cachedValue by Delegates.notNull<List<Child>>()

        init {
            getValue(entity, EagerReferrers<*, *, *>::cachedReferrers)
        }

        operator fun getValue(o: Parent, desc: KProperty<*>): List<Child> {
            return if (TransactionManager.currentOrNull() == null)
                cachedValue
            else {
                cachedReferrers.getValue(o, desc).toList().apply {
                    cachedValue = this
                }
            }
        }
    }

    infix fun <ID : Comparable<ID>, Parent : Entity<ID>, Child : Entity<ID>> Referrers<ID, Parent, ID, Child>.eager(e: Parent) = EagerReferrers(this, e)
}
