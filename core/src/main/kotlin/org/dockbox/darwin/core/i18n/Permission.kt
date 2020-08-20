package org.dockbox.darwin.core.i18n

enum class Permission(private var value: String): I18NRegistry {

    GLOBAL_BYPASS("darwin.admin.bypass-all");

    override fun getValue(): String {
        return this.value
    }

    companion object {
        private val map = values().associateBy(Permission::value)

        fun of(perm: String): I18NRegistry {
            return if (map.containsKey(perm))
                map[perm] ?: error("Node key is present but was absent on return")
            else return object :I18NRegistry {
                override fun getValue(): String = perm
            }
        }
    }
}