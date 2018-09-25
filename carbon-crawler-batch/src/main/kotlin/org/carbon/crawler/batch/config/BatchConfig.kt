package org.carbon.crawler.batch.config

import org.carbon.crawler.batch.extend.spring.batch.then
import org.carbon.crawler.batch.step.StepMarker
import org.carbon.crawler.batch.step.crawle.CrawlOrderProcessor
import org.carbon.crawler.batch.step.crawle.CrawlScrapingProcessor
import org.carbon.crawler.batch.step.crawle.DictionaryReader
import org.carbon.crawler.batch.step.crawle.PageWriter
import org.carbon.crawler.batch.step.crawle.item.DictionaryItem
import org.carbon.crawler.batch.step.crawle.item.PageChunkItem
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * @author Soda 2018/07/22.
 */
@Configuration
@ComponentScan(basePackageClasses = [StepMarker::class])
@EnableBatchProcessing
class BatchConfig constructor(
        historyDataSource: DataSource,
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactory: StepBuilderFactory,
        private val dictionaryReader: DictionaryReader,
        private val crawlOrderProcessor: CrawlOrderProcessor,
        private val crawlScrapingProcessor: CrawlScrapingProcessor,
        private val pageWriter: PageWriter
) : DefaultBatchConfigurer(historyDataSource) {
    @Bean
    fun stepCrawl(): Step = stepBuilderFactory.get("step:crawl")
            .chunk<DictionaryItem, PageChunkItem>(10)
            .reader(dictionaryReader)
            .processor(crawlOrderProcessor then crawlScrapingProcessor)
            .writer(pageWriter)
            .build()

    @Bean
    fun jobCrawl(): Job = jobBuilderFactory.get("jog:crawl")
            .incrementer(RunIdIncrementer())
            .start(stepCrawl())
            .build()
}
