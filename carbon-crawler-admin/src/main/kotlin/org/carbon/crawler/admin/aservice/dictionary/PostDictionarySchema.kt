package org.carbon.crawler.admin.aservice.dictionary

import org.carbon.crawler.admin.V1
import org.carbon.crawler.admin.aservice.validation.Definition
import org.carbon.crawler.admin.aservice.validation.Validated

object PostDictionarySchema : Validated<V1.PostDictionary.Body> {
    override val def: Definition<V1.PostDictionary.Body> = {
    }
}
