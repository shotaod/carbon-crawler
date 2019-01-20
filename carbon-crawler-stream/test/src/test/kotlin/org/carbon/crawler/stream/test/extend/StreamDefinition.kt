package org.carbon.crawler.stream.test.extend

/**
 * Builder of deployable SCDF stream definitions which honors elements of SCDF Shell DSL.
 *
 *
 * Example usage:
 *
 *
 * <pre>
 * `StreamDefinition.builder("TICKTOCK")
 * .definition("time | log")
 * .addProperty("app.log.log.expression", "'TICKTOCK - TIMESTAMP: '.concat(payload)")
 * .build();
` *
</pre> *
 *
 * @author Vinicius Carvalho
 * @author Oleg Zhurakousky
 */
data class StreamDefinition(
    val name: String,
    val dsl: String,
    val deploymentProperties: Map<String, String>
) {
    private val _applications = org.springframework.cloud.dataflow.core.StreamDefinition(name, dsl)
        .appDefinitions
        .map { it.registeredAppName to org.carbon.crawler.stream.test.extend.Application(it.registeredAppName) }
        .toMap()

    val applications get() = _applications.values

    operator fun get(name: String): Application? = _applications[name]
}
