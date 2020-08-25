package org.dockbox.darwin.core.i18n

interface I18nService {

    fun injectDocumentedTranslations()

    fun getEntry(key: String): I18NRegistry
}
