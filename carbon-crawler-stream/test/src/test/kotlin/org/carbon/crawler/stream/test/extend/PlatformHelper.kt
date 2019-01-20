package org.carbon.crawler.stream.test.extend

import org.springframework.cloud.dataflow.rest.client.RuntimeOperations
import org.springframework.cloud.dataflow.rest.resource.AppStatusResource

/**
 * @author Glenn Renfro
 * @author Thomas Risberg
 * @author Vinicius Carvalho
 */
class PlatformHelper(private var operations: RuntimeOperations) {

    companion object {
        @Suppress("PrivatePropertyName")
        private val URL = "url"
    }

    fun setUrlsForStream(stream: StreamDefinition): Boolean {
        val statsIterator = operations.status().iterator()
        val numberOfAppsInStream = stream.applications.size
        var numberOfAppsWithUrls = 0
        while (statsIterator.hasNext()) {
            val appStatus = statsIterator.next()
            for (application in stream.applications) {
                if (appStatus.deploymentId.toLowerCase().contains(stream.name.toLowerCase()) && extractName(appStatus.deploymentId).endsWith(application.name)) {
                    if (!setUrlForApplication(application, appStatus)) {
                        return false
                    } else {
                        setInstanceUrlsForApplication(application, appStatus)
                        numberOfAppsWithUrls++
                    }
                }
            }
        }
        return numberOfAppsInStream == numberOfAppsWithUrls
    }

    /**
     * removes the '-v1' like suffix from the deploymentId like 'HTTP-TEST.log-v1'
     */
    private fun extractName(appName: String): String {
        return if (!appName.contains("-v")) {
            appName
        } else appName.split("-v".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
    }

    /**
     * Set URL for application based on 'url' attribute from the instances. Can be
     * overridden by platform implementation that do not provide the 'url' instance
     * attribute.
     *
     * @param application
     * @param appStatus
     * @return whether url was set
     */
    private fun setUrlForApplication(application: Application, appStatus: AppStatusResource): Boolean {
        val resourceIterator = appStatus.instances.iterator()
        while (resourceIterator.hasNext()) {
            val resource = resourceIterator.next()
            if (resource.attributes.containsKey(URL)) {
                application.url = resource.attributes[URL]
                break
            } else {
                return false
            }
        }
        return true
    }

    /**
     * Set URL the instance URLs based on 'url' attribute from each instances. Can be
     * overridden by platform implementations that do not provide the 'url' instance
     * attribute.
     *
     * @param application
     * @param appStatus
     * @return whether any url was set
     */
    private fun setInstanceUrlsForApplication(application: Application, appStatus: AppStatusResource) {
        val resourceIterator = appStatus.instances.iterator()
        while (resourceIterator.hasNext()) {
            val resource = resourceIterator.next()
            resource.attributes[URL]
                ?.let { application.addInstanceUrl(resource.instanceId, it) }
        }
    }
}
