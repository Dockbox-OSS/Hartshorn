package org.dockbox.darwin.core.i18n

class SimpleI18NRegistry(private var value: String) : I18NRegistry {

    override fun getValue(): String = this.value

    // TODO: Add config key?
    override fun getValue(lang: Languages): String = this.value

    override fun setValue(value: String) {
        this.value = value
    }
}
