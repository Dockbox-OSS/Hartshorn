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

package org.dockbox.darwin.core.i18n.entry

import org.dockbox.darwin.core.i18n.common.Language
import org.dockbox.darwin.core.i18n.common.ResourceEntry
import org.dockbox.darwin.core.i18n.common.ResourceService
import org.dockbox.darwin.core.server.Server

class ExternalResourceEntry(private var value: String, private var key: String) : ResourceEntry {

    private var resourceMap: Map<Language, String> = Server.getInstance(ResourceService::class.java).getTranslations(this.key)

    fun getKey(): String = Server.getInstance(ResourceService::class.java).createValidKey(key)

    override fun getValue(): String = this.value

    override fun getValue(lang: Language): String {
        return if (resourceMap.containsKey(lang)) resourceMap[lang]!!
        else this.value
    }

    override fun setValue(value: String) {
        this.value = value
    }
}
