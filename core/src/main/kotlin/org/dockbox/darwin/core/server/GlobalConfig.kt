package org.dockbox.darwin.core.server

import org.dockbox.darwin.core.i18n.Languages

interface GlobalConfig {

    fun <T> getSetting(key: String): T
    fun getDefaultLanguage(): Languages

}
