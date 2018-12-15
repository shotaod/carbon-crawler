package org.carbon.crawler.admin.www.v1.dictionary

import org.carbon.crawler.admin.extend.carbon.validation.NumberMax
import org.carbon.crawler.admin.extend.carbon.validation.NumberMin
import org.carbon.crawler.admin.extend.carbon.validation.NumberString
import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.and
import org.carbon.objects.validation.matcher.be
import org.carbon.objects.validation.matcher.mayBe


class GetDictionaryParameter(
        val _page: String,
        val _size: String
) : Validated<GetDictionaryParameter> by GetDictionaryParameterSchema {
    companion object {
        fun get(page: String?, size: String?): GetDictionaryParameter =
                GetDictionaryParameter(page ?: "0", size ?: "10")
    }

    operator fun component1(): Int = _page.toInt()
    operator fun component2(): Int = _size.toInt()
}

object GetDictionaryParameterSchema : Validated<GetDictionaryParameter> {
    override val def: Definition<GetDictionaryParameter> = { param ->
        param._page.should {
            and(
                    it be NumberString,
                    it.toIntOrNull() mayBe NumberMin(0)
            )
        } otherwise "page".invalidate()

        param._size.should {
            val num = it.toIntOrNull()
            and(
                    it mayBe NumberString,
                    num mayBe NumberMin(0),
                    num mayBe NumberMax(99)
            )
        } otherwise "size".invalidate()

    }
}
