package org.dockbox.darwin.core.i18n

interface I18nService {

    fun inject()

    fun getMap(lang: Languages): Map<String, I18NRegistry>
    fun getTranslations(lang: Languages): Map<String, I18NRegistry>
    fun getPermissions(lang: Languages): Map<String, I18NRegistry>

    fun getEntry(key: String, lang: Languages): I18NRegistry?
    fun addTranslation(key:String, lang: Languages, reg: I18NRegistry)
}
