package org.dockbox.darwin.core.i18n

import org.dockbox.darwin.core.server.Server

enum class Permission(private var value: String): I18NRegistry {

    GLOBAL_BYPASS("darwin.admin.bypass-all");

    override fun setValue(value: String) {
        this.value = value
    }

    override fun getValue(): String {
        return getValue(Server.getServer().getGlobalConfig().getDefaultLanguage()) // TODO: Default language config
    }

    override fun getValue(lang: Languages): String {
        return Server.getInstance(I18nService::class.java).getPermissions(lang)[this.name]?.getValue() ?: this.value
    }

    companion object {
        private val map = values().associateBy(Permission::value)

        fun of(perm: String): I18NRegistry {
            return if (map.containsKey(perm))
                map[perm] ?: error("Node key is present but was absent on return")
            else return object :I18NRegistry {
                override fun getValue(): String = perm
                override fun getValue(lang: Languages): String = perm
                override fun setValue(value: String) = Unit
            }
        }
    }
}
