package org.carbon.crawler.batch.extend.spring.batch

import org.springframework.batch.item.ItemProcessor

/**
 * @author Soda 2018/08/04.
 */
class CompositeProcessor<I, M, O>(
        private val inProcessor: ItemProcessor<I, M>,
        private val outProcessor: ItemProcessor<M, O>) : ItemProcessor<I, O> {
    override fun process(item: I): O = inProcessor.process(item) then outProcessor
    private infix fun <TI, TO> TI.then(processor: ItemProcessor<TI, TO>): TO = processor.process(this)
}

infix fun <I, M, O> ItemProcessor<I, M>.then(next: ItemProcessor<M, O>): ItemProcessor<I, O> = CompositeProcessor(this, next)
