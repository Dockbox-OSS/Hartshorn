/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.darwin.core.util.player

import org.dockbox.darwin.core.i18n.Languages
import org.dockbox.darwin.core.objects.user.Player
import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.server.config.ConfigKeys
import org.dockbox.darwin.core.util.files.DataManager
import java.util.*

abstract class PlayerStorageService {

    abstract fun getOnlinePlayers(): List<Player>
    abstract fun getPlayer(name: String): Optional<Player>
    abstract fun getPlayer(uuid: UUID): Optional<Player>

    fun setLanguagePreference(uuid: UUID, lang: Languages) {
        val data = getUserData(uuid).toMutableMap()
        data[ConfigKeys.PLAYER_LANGUAGE.key] = lang.code
        Server.getInstance(DataManager::class.java).writeToDataFile(Server::class.java, data, uuid.toString().toLowerCase())
    }

    fun getLanguagePreference(uuid: UUID): Languages {
        val lang = getUserData(uuid).getOrDefault(ConfigKeys.PLAYER_LANGUAGE.key, Server.getServer().getGlobalConfig().getDefaultLanguage().code).toString()

        return try {
            Languages.valueOf(lang.toUpperCase())
        } catch (e: IllegalArgumentException) {
            Server.getServer().getGlobalConfig().getDefaultLanguage()
        } catch (e: NullPointerException) {
            Server.getServer().getGlobalConfig().getDefaultLanguage()
        }
    }

    private fun getUserData(uuid: UUID): Map<String, Any> {
        return Server
                .getInstance(DataManager::class.java)
                .getDataFileContents(Server::class.java, uuid.toString().toLowerCase())
    }
}
