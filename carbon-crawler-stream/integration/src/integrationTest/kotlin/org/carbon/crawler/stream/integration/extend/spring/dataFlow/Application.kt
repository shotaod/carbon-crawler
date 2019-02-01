package org.carbon.crawler.stream.integration.extend.spring.dataFlow

import java.util.HashMap

/**
 * Contains the metadata required for the acceptance test execute calls against the
 * application's end points.
 *
 * @author Glenn Renfro
 */
class Application
/**
 * Initializes the application instance.
 *
 * @param definition Establish the registered app name and the required properties.
 */
(definition: String) {

    /**
     * Retrieve the uri for this application.
     */
    var url: String? = null

    /**
     * The host where the application can be reached.
     */
    val instanceUrls: MutableMap<String, String> = HashMap()

    /**
     * Retrieve the definition for this application.
     * @return the definition
     */
    var definition: String
        protected set

    /**
     * Retrieve the name of the app for this definition.
     * @return name of app
     */
    val name: String
        get() = if (definition.trim { it <= ' ' }.contains(" ")) {
            definition.trim { it <= ' ' }.substring(0, definition.indexOf(" "))
        } else this.definition

    init {
        this.definition = definition
    }

    override fun toString(): String {
        return this.definition
    }

    /**
     * The add URL where the application instance can be reached.
     * @param instanceId
     * @param url
     */
    fun addInstanceUrl(instanceId: String, url: String) {
        this.instanceUrls[instanceId] = url
    }
}
