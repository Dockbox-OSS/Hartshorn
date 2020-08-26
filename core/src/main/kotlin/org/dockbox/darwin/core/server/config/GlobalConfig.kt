package org.dockbox.darwin.core.server.config

import org.dockbox.darwin.core.i18n.Languages

interface GlobalConfig {

    fun getSetting(key: String): String?
    fun getDefaultLanguage(): Languages
    fun getStacktracesAllowed(): Boolean
    fun getExceptionLevel(): ExceptionLevels

    enum class ConfigKeys(val key: String) {
        PLAYER_LANGUAGE("language"),
        GLOBAL_CONFIG("global.language"),
        ALLOW_STACKTRACES("exceptions.stacktraces"),
        EXCEPTION_LEVEL("exceptions.level")
    }

    enum class ExceptionLevels {
        FRIENDLY, MINIMAL
    }

}
