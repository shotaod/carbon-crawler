package org.carbon.crawler.stream.integration.extend.spring.dataFlow

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.dataflow.rest.client.DataFlowTemplate
import org.springframework.cloud.dataflow.rest.client.RuntimeOperations
import org.springframework.cloud.dataflow.rest.client.StreamOperations
import org.springframework.cloud.dataflow.rest.resource.StreamDefinitionResource
import org.springframework.cloud.dataflow.rest.util.DeploymentPropertiesUtils
import org.springframework.cloud.skipper.domain.PackageIdentifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.io.FileInputStream
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.Properties

/**
 * Abstract base class that is used by stream acceptance tests. This class contains
 * commonly used utility methods for acceptance tests as well as the ability to dump logs
 * of apps when a stream acceptance test fails.
 * @author Glenn Renfro
 * @author Thomas Risberg
 * @author Vinicius Carvalho
 * @author Christian Tzolov
 * @author Ilayaperumal Gopinathan
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration
@EnableConfigurationProperties(TestProperties::class)
abstract class AbstractStreamTests : InitializingBean {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var property: TestProperties

    private lateinit var restTemplate: RestTemplate

    private lateinit var streamOperations: StreamOperations

    private lateinit var runtimeOperations: RuntimeOperations

    private lateinit var platformHelper: PlatformHelper

    /**
     * Ensures that all streams are destroyed regardless if the test was successful or failed.
     */
    @AfterEach
    fun afterEach() {
        logger.info("Destroy all streams")
        streamOperations.destroyAll()
    }

    override fun afterPropertiesSet() {
        val dataFlowOperations = try {
            DataFlowTemplate(URI(property.deployerUri))
        } catch (uriException: URISyntaxException) {
            throw IllegalStateException(uriException)
        }
        streamOperations = dataFlowOperations.streamOperations()
        runtimeOperations = dataFlowOperations.runtimeOperations()
        platformHelper = PlatformHelper(runtimeOperations)
        restTemplate = RestTemplate()

        registerApps(dataFlowOperations)
    }

    private fun registerApps(dataFlowOperations: DataFlowTemplate) {
        val resource = property.defFile
        logger.info("Importing stream apps from uri resource: $resource")
        val properties = FileInputStream(resource).use {
            Properties().apply { load(it) }
        }
        dataFlowOperations.appRegistryOperations().registerAll(properties, true)
    }

    // ______________________________________________________
    //
    // @ Test support functions
    /**
     * Deploys the stream specified to the Spring Cloud Data Flow instance.
     * @param stream the stream object containing the stream definition.
     */
    protected fun deployStream(stream: StreamDefinition) {
        logger.info("Creating stream '${stream.name}'")
        streamOperations.createStream(stream.name, stream.dsl, false)
        val streamProperties = mapOf(
            "app.*.logging.file" to "\${PID}-test.log",
            "app.*.endpoints.logfile.sensitive" to "false",
            // Specific to Boot 2.x applications, also allows access without authentication
            "app.*.management.endpoints.web.exposure.include" to "*"
        ) + stream.deploymentProperties

        logger.info("Deploying stream '${stream.name}' with properties: $streamProperties")
        streamOperations.deploy(stream.name, streamProperties)
        streamAvailable(stream)
    }

    /**
     * Updates the stream specified to the Spring Cloud Data Flow instance.
     * @param stream the stream object containing the stream definition.
     * @return the available StreamDefinition REST resource.
     */
    protected fun updateStream(stream: StreamDefinition, properties: String, appNames: List<String>): StreamDefinitionResource? {
        val propertiesToUse: Map<String, String>?
        try {
            propertiesToUse = DeploymentPropertiesUtils.parseDeploymentProperties(properties, null, 0)
        } catch (e: IOException) {
            throw RuntimeException(e.message)
        }

        val streamName = stream.name
        val packageIdentifier = PackageIdentifier()
        packageIdentifier.packageName = stream.name
        logger.info("Updating stream '${stream.name}'")
        streamOperations.updateStream(streamName, streamName, packageIdentifier, propertiesToUse, false, appNames)
        return streamAvailable(stream)
    }

    /**
     * Rollback the stream to a specific version.
     * @param stream the stream object containing the stream definition.
     * @param streamVersion the stream version to rollback to
     * @return the available StreamDefinition REST resource.
     */
    protected fun rollbackStream(stream: StreamDefinition, streamVersion: Int): StreamDefinitionResource? {
        logger.info("Rolling back the stream '" + stream.name + "' to the version " + streamVersion)
        this.streamOperations.rollbackStream(stream.name, streamVersion)
        return streamAvailable(stream)
    }

    /**
     * Post data to the app specified.
     * @param app the app that will receive the data.
     * @param message the data to be sent to the app.
     */
    protected fun httpPostData(app: Application, message: String) {
        restTemplate.postForObject(String.format(app.url!!), message, String::class.java)
    }

    /**
     * Waits the specified period of time for an entry to appear in the logfile of the
     * specified app.
     * @param app the app that is being monitored for a specific entry.
     * @param entries the array of values being monitored for.
     * @return
     */
    protected fun waitForLogEntry(app: Application, vararg entries: String): Boolean {
        val commaEntries = entries.joinToString(",")
        logger.info("Looking for '$commaEntries' in logfile for ${app.definition}")
        val timeout = System.currentTimeMillis() + property.maxWaitTime.toInt() * 1000
        var exists = false
        var instance = "?"
        val logData = mutableMapOf<String, String?>()

        while (!exists && System.currentTimeMillis() < timeout) {
            for (appInstance in app.instanceUrls.keys) {
                if (!exists) {
                    logger.info("Requesting log for app $appInstance")
                    val log = getLog(app.instanceUrls[appInstance]!!)
                    logData[appInstance] = log
                    if (log != null) {
                        if (entries.all { it in log }) {
                            exists = true
                            instance = appInstance
                        }
                    } else {
                        logger.info("Polling to get log file. Remaining poll time = "
                            + java.lang.Long.toString((timeout - System.currentTimeMillis()) / 1000) + " seconds.")
                    }
                }
            }
            pause()
        }
        if (exists) {
            logger.info("Matched all '$commaEntries' in logfile for instance $instance of app ${app.definition}")
        } else {
            logger.error("ERROR: Couldn't find '$commaEntries' in logfiles for ${app.definition}. Dumping log files.\n\n")
            for ((key, value) in logData) {
                logger.error("<logFile> =================")
                logger.error("Log File for $key\n$value")
                logger.error("</logFile> ================\n")
            }
        }
        return exists
    }

    /**
     * Retrieve the log for an app.
     * @param url the app URL to query to retrieve the log.
     * @return String containing the contents of the log or 'null' if not found.
     */
    private fun getLog(url: String): String? {

        val logFileUrl = getLogFileUrl(url)
        var log: String? = null
        try {
            log = restTemplate.getForObject(logFileUrl)
            if (log === null) {
                logger.info("Unable to retrieve logfile from '$logFileUrl")
            } else {
                logger.info("Retrieved logfile from '$logFileUrl")
            }
        } catch (e: HttpClientErrorException) {
            logger.info("Failed to access logfile from '$logFileUrl' due to : ${e.message}")
        } catch (e: Exception) {
            logger.warn("Error while trying to access logfile from '$logFileUrl' due to : $e")
        }

        return log
    }

    private fun getLogFileUrl(url: String): String {

        // check if this is a boot 2.x application, and if so, follow 2.x url conventions to access log file.
        val actuatorUrl = "$url/actuator"
        return try {
            restTemplate.exchange(actuatorUrl, HttpMethod.GET, null, String::class.java)
        } catch (e: Exception) {
            null
        }
            ?.takeIf { it.statusCode == HttpStatus.OK }
            ?.let { "$url/actuator/logfile" }
            ?: "$url/logfile"
    }


    // ______________________________________________________
    //
    // @ Test Support Common Functions
    /**
     * Waits for the stream to be deployed and once deployed the function returns control else
     * it throws an IllegalStateException.
     * @param stream the stream that is to be monitored.
     * @return the available StreamDefinition REST resource.
     */
    private fun streamAvailable(stream: StreamDefinition): StreamDefinitionResource? {
        var streamStarted = false
        var attempt = 0
        var status = "not present"
        var resource: StreamDefinitionResource? = null
        while (!streamStarted && attempt < property.deployPauseRetries.toInt()) {
            resource = streamOperations.list().content
                .find { it.name == stream.name }
                ?.also {
                    status = it.status
                    logger.info("Checking: Stream=${stream.name}, status = ${it.status}, status description = ${it.statusDescription}")
                }
                ?.takeIf {
                    it.status == "deployed" && platformHelper.setUrlsForStream(stream) // url is available ?
                }
                ?.also {
                    streamStarted = true
                }

            if (resource != null) break
            attempt++
            logger.info("Sleeping to check status of Stream=" + stream.name)
            pause()
        }

        if (streamStarted) {
            logger.info(String.format("Stream '%s' started with status: %s", stream.name, status))
            for (app in stream.applications) {
                logger.info("App '${app.name}' has instances: ${app.instanceUrls}")
            }
        } else {
            var statusDescription = "null"
            if (resource != null) {
                statusDescription = resource.statusDescription
            }
            logger.info("Stream '${stream.name}' NOT started with status: $status.  Description = $statusDescription")

            throw IllegalStateException("Unable to start stream ${stream.name}.  Definition = ${stream.dsl}")
        }
        return resource
    }

    /**
     * Pauses the run to for a period of seconds as specified by the the pauseTime
     * attribute.
     */
    private fun pause() {
        try {
            Thread.sleep((property.pauseTime.toInt() * 1000).toLong())
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IllegalStateException(e.message, e)
        }
    }
}
