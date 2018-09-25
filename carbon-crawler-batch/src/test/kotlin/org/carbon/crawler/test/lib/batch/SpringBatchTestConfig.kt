package org.carbon.crawler.test.lib.batch

import org.carbon.crawler.batch.step.StepMarker
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean
import org.springframework.batch.support.transaction.ResourcelessTransactionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * @author Soda 2018/08/05.
 */
@ComponentScan(basePackageClasses = [StepMarker::class])
@Configuration
class SpringBatchTestConfig {
    @Bean
    fun jobRepository(): JobRepository = MapJobRepositoryFactoryBean(ResourcelessTransactionManager()).`object`!!
}