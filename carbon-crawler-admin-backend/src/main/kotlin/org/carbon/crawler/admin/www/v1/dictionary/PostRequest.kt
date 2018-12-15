package org.carbon.crawler.admin.www.v1.dictionary

import org.carbon.crawler.admin.extend.carbon.validation.LengthMax
import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.URL
import org.carbon.objects.validation.matcher.be
import org.carbon.objects.validation.matcher.max
import org.carbon.objects.validation.matcher.mayBe

data class PostDictionaryBody(
        val url: String,
        val title: String,
        val memo: String?)
    : Validated<PostDictionaryBody> by PostDictionaryBodySchema

object PostDictionaryBodySchema : Validated<PostDictionaryBody> {
    override val def: Definition<PostDictionaryBody> = { dictBody ->
        dictBody.url should { it be URL } otherwise "url".invalidate()
        dictBody.title should { it max 255 } otherwise "title".invalidate()
        dictBody.memo should { it mayBe LengthMax(1023) } otherwise "memo".invalidate()
    }
}