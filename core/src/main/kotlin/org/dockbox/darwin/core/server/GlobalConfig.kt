package org.dockbox.darwin.core.server

import org.dockbox.darwin.core.i18n.Languages

interface GlobalConfig {

    fun getSetting(key: String): String?
    fun getDefaultLanguage(): Languages

}
