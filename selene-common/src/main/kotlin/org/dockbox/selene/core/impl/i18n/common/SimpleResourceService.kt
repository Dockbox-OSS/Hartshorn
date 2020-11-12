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

package org.dockbox.selene.core.impl.i18n.common

import com.google.inject.Singleton
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.dockbox.selene.core.i18n.common.Language
import org.dockbox.selene.core.i18n.common.ResourceService
import org.dockbox.selene.core.i18n.entry.ExternalResourceEntry
import org.dockbox.selene.core.objects.optional.Exceptional
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.server.ServerReference
import org.dockbox.selene.core.util.files.ConfigurateManager

@Singleton
class SimpleResourceService : ResourceService, ServerReference() {

    private val resourceMaps: EnumMap<Language, Map<String, String>> = EnumMap(Language::class.java)
    private val knownEntries: MutableList<ExternalResourceEntry> = CopyOnWriteArrayList()

    override fun init() {
        getResourceMap(Selene.getServer().getGlobalConfig().getDefaultLanguage())
    }

    @ConfigSerializable
    class ResourceConfig {
        @Setting
        var translations: Map<String, String> = HashMap()
    }

    override fun getResourceMap(lang: Language): Map<String, String> {
        if (resourceMaps.containsKey(lang)) return resourceMaps[lang]!!

        val cm = Selene.getInstance(ConfigurateManager::class.java)

        val resourceConfig = cm.getFileContent(
                cm.getConfigFile(super.getExtension(Selene::class.java)!!, lang.code),
                ResourceConfig::class.java)

        val resourceMap = ConcurrentHashMap<String, String>()
        resourceConfig.ifPresent {
            it.translations.forEach { (k, v) ->
                run {
                    resourceMap[k] = v
                }
            }
        }
        resourceMaps[lang] = resourceMap
        return resourceMap
    }

    // TODO: Populate if not exists
    override fun getTranslations(entry: ExternalResourceEntry): Map<Language, String> {
        val key = entry.getKey()
        knownEntries.add(entry)
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

    override fun getExternalResource(key: String): Exceptional<ExternalResourceEntry> {
        return Exceptional.ofNullable(knownEntries.firstOrNull { it.getKey() == createValidKey(key) })
    }
}
