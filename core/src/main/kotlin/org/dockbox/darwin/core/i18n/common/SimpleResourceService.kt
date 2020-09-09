/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.darwin.core.i18n.common

import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.util.extension.Extension
import org.dockbox.darwin.core.util.files.DataManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Extension(id = "i18n", authors = ["GuusLieben"], description = "Provides a simple implementation of I18N", name = "Simple I18N")
class SimpleResourceService : ResourceService {

    var resourceMaps: EnumMap<Language, Map<String, String>> = EnumMap(Language::class.java)

    override fun init() {
        getResourceMap(Server.getServer().getGlobalConfig().getDefaultLanguage())
    }

    override fun getResourceMap(lang: Language): Map<String, String> {
        if (resourceMaps.containsKey(lang)) return resourceMaps[lang]!!

        val translationDataMap = Server.getInstance(DataManager::class.java).getDataFileContents(this, lang.code)
        val resourceMap = ConcurrentHashMap<String, String>()
        translationDataMap.forEach { (k, v) ->
            run {
                resourceMap[k] = v.toString()
            }
        }
        resourceMaps[lang] = resourceMap
        return resourceMap
    }

    override fun getTranslations(key: String): Map<Language, String> {
        val resourceMap = ConcurrentHashMap<Language, String>()
        resourceMaps.forEach { (k, v) ->
            run {
                if (v.containsKey(key)) {
                    resourceMap[k] = v[key] ?: "$$key"
                }
            }
        }
        return resourceMap
    }

    override fun createValidKey(raw: String): String = raw
            .replace("-".toRegex(), "_")
            .replace("\\.".toRegex(), "_")
            .replace("/".toRegex(), "_")
            .toUpperCase()
}
