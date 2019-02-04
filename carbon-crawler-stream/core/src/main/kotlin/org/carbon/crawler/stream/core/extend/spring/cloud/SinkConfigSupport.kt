package org.carbon.crawler.stream.core.extend.spring.cloud

typealias SinkFunction<PAYLOAD> = (PAYLOAD) -> Unit
