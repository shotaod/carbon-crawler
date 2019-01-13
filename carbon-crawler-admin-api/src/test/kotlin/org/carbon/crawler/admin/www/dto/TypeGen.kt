package org.carbon.crawler.admin.www.dto

import me.ntrrgc.tsGenerator.TypeScriptGenerator
import me.ntrrgc.tsGenerator.VoidType
import org.carbon.crawler.admin.www.Page
import org.carbon.crawler.admin.www.v1.query.QueryAddRequest
import org.carbon.crawler.admin.www.v1.query.QueryResponse
import org.carbon.crawler.admin.www.v1.snap.SnapResponse
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime

object TypeGen {
    private val logger = LoggerFactory.getLogger(TypeGen::class.java)
    @Test
    fun generate() {
        val innerDeclaration = TypeScriptGenerator(
            rootClasses = listOf(
                Page::class,
                QueryResponse::class,
                QueryAddRequest::class,
                SnapResponse::class
            ),
            mappings = mapOf(
                LocalDateTime::class to "Date",
                LocalDate::class to "Date"
            ),
            voidType = VoidType.UNDEFINED
        ).definitionsText
        val margin = "\n|    "
        logger.info("""
        |
        |--------------------------------------------------
        |kotlin data class -to-> typescript definition
        |--------------------------------------------------
        |declare namespace Fetch {
        $margin${innerDeclaration.lines().joinToString(separator = margin)}
        |}
        """.trimMargin().replace("Validated {", "Validated<{}> {"))
    }
}