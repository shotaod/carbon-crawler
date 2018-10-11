package org.carbon.crawler.model

import org.carbon.crawler.model.extend.exposed.setupDataSource

/**
 * @author Soda 2018/10/09.
 */
fun cleanDatabase() = setupDataSource().connection.use {
    it.createStatement().execute("DROP ALL OBJECTS")
}