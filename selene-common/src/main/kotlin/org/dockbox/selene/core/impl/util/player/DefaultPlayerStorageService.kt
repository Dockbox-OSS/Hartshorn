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

package org.dockbox.selene.core.impl.util.player

import java.util.*
import org.dockbox.selene.core.i18n.common.Language
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.server.config.ConfigKeys
import org.dockbox.selene.core.util.files.DataManager
import org.dockbox.selene.core.util.player.PlayerStorageService

abstract class DefaultPlayerStorageService : PlayerStorageService {

    override fun setLanguagePreference(uuid: UUID, lang: Language) {
        val data = getUserData(uuid).toMutableMap()
        data[ConfigKeys.PLAYER_LANGUAGE.key] = lang.code
        Selene.getInstance(DataManager::class.java).writeToDataFile(Selene::class.java, data, uuid.toString().toLowerCase())
    }

    override fun getLanguagePreference(uuid: UUID): Language {
        val lang = getUserData(uuid).getOrDefault(ConfigKeys.PLAYER_LANGUAGE.key, Selene.getServer().globalConfig.getDefaultLanguage().code).toString()

        return try {
            Language.valueOf(lang.toUpperCase())
        } catch (e: IllegalArgumentException) {
            Selene.getServer().globalConfig.getDefaultLanguage()
        } catch (e: NullPointerException) {
            Selene.getServer().globalConfig.getDefaultLanguage()
        }
    }

    private fun getUserData(uuid: UUID): Map<String, Any> {
        return Selene
                .getInstance(DataManager::class.java)
                .getDataFileContents(Selene::class.java, uuid.toString().toLowerCase())
    }
}
