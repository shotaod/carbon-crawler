package org.carbon.crawler.admin.aservice.dictionary

import org.carbon.crawler.admin.V1
import org.carbon.crawler.admin.aservice.validation.Definition
import org.carbon.crawler.admin.aservice.validation.Validated

object GetDictionarySchema : Validated<V1.GetDictionary> {
    override val def: Definition<V1.GetDictionary> = {
    }
}
