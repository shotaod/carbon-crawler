package org.carbon.crawler.admin.extend.carbon.validation

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.util.StdConverter
import org.carbon.objects.validation.evaluation.Key
import org.carbon.objects.validation.evaluation.source.Code

object CodeConverter : StdConverter<Code, String>() {
    override fun convert(value: Code?): String? = value?.canonicalName
}

object KeyConverter : StdConverter<Key, String>() {
    override fun convert(value: Key?): String? = value?.qualifiedName
}

object CarbonValidationModule : SimpleModule() {
    init {
        addSerializer(Code::class.java, StdDelegatingSerializer(CodeConverter))
        addSerializer(Key::class.java, StdDelegatingSerializer(KeyConverter))
    }
}