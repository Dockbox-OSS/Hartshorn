package org.dockbox.darwin.core.i18n

import org.dockbox.darwin.core.annotations.Module
import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.util.files.DataManager
import java.util.*
import kotlin.collections.HashMap

@Module(id = "i18n", authors = ["GuusLieben"], description = "Provides a simple implementation of I18N", name = "Simple I18N")
class SimpleI18NService : I18nService {

    var translationMaps: EnumMap<Languages, Map<String, I18N>> = EnumMap(Languages::class.java)
    var permissionMaps: EnumMap<Languages, Map<String, Permission>> = EnumMap(Languages::class.java)

    override fun inject() {
        getPermissions(Server.getServer().getGlobalConfig().getDefaultLanguage())
        getTranslations(Server.getServer().getGlobalConfig().getDefaultLanguage())
    }

    override fun getMap(lang: Languages): Map<String, I18NRegistry> {
        return HashMap<String, I18NRegistry>().also {
            it.putAll(getTranslations(lang))
            it.putAll(getPermissions(lang))
        }
    }

    override fun getTranslations(lang: Languages): Map<String, I18N> {
        if (translationMaps.containsKey(lang)) return translationMaps[lang]!!

        val i18nMap = Server.getInstance(DataManager::class.java).getDataContents(this, lang.code)
        val translationMap = i18nMap["translations"]
        @Suppress("UNCHECKED_CAST")
        val map = getReturnableMap(translationMap) { k -> I18N.valueOf(k) } as Map<String, I18N>
        if (!translationMaps.containsKey(lang)) translationMaps[lang] = map
        return map
    }

    override fun getPermissions(lang: Languages): Map<String, Permission> {
        if (permissionMaps.containsKey(lang)) return permissionMaps[lang]!!

        val i18nMap = Server.getInstance(DataManager::class.java).getDataContents(this, lang.code)
        val permissionMap = i18nMap["permissions"]
        @Suppress("UNCHECKED_CAST")
        val map = getReturnableMap(permissionMap) { k -> Permission.valueOf(k) } as Map<String, Permission>
        if (!permissionMaps.containsKey(lang)) permissionMaps[lang] = map
        return map
    }

    private fun getReturnableMap(mapIn: Any?, converter: (String) -> I18NRegistry): Map<String, I18NRegistry> {
        val returnableMap = HashMap<String, I18NRegistry>()
        if (mapIn is Map<*, *>) mapIn.forEach { (k: Any?, v: Any?) ->
            run {
                if (k is String && v is String)
                    returnableMap[k] = converter.invoke(convertKey(k)).also {
                        it.setValue(v)
                    }
            }
        }
        return returnableMap
    }

    override fun getEntry(key: String, lang: Languages): I18NRegistry? {
        val shadow = convertKey(key)

        try {
            return I18N.valueOf(shadow)
        } catch (ignored: IllegalArgumentException) {
        } catch (ignored: NullPointerException) {
        }

        try {
            return Permission.valueOf(shadow)
        } catch (ignored: IllegalArgumentException) {
        } catch (ignored: NullPointerException) {
        }

        return null
    }

    private fun convertKey(raw: String): String = raw
            .replace("-".toRegex(), "_")
            .replace("\\.".toRegex(), "_")
            .replace("/".toRegex(), "_")
            .toUpperCase()
}
