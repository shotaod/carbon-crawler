package org.carbon.crawler.batch.step.support

abstract class ChunkItem<ITEM, T : ChunkItem<ITEM, T>>(var items: List<ITEM>) {
    constructor() : this(emptyList())

    abstract fun assign(t: ITEM): T
}
