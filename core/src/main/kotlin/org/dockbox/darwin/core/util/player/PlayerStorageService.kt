package org.dockbox.darwin.core.util.player

import org.dockbox.darwin.core.i18n.Languages
import org.dockbox.darwin.core.objects.user.Player
import org.dockbox.darwin.core.server.config.GlobalConfig
import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.util.files.DataManager
import java.util.*

abstract class PlayerStorageService {

    abstract fun getOnlinePlayers(): List<Player>
    abstract fun getPlayer(name: String): Optional<Player>
    abstract fun getPlayer(uuid: UUID): Optional<Player>

    fun setLanguagePreference(uuid: UUID, lang: Languages) {
        val data = getUserData(uuid).toMutableMap()
        data[GlobalConfig.ConfigKeys.PLAYER_LANGUAGE.key] = lang.code
        Server.getInstance(DataManager::class.java).writeToData(Server::class.java, data, uuid.toString().toLowerCase())
    }

    fun getLanguagePreference(uuid: UUID): Languages {
        val lang = getUserData(uuid).getOrDefault(GlobalConfig.ConfigKeys.PLAYER_LANGUAGE.key, Server.getServer().getGlobalConfig().getDefaultLanguage().code).toString()

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
                .getDataContents(Server::class.java, uuid.toString().toLowerCase())
    }
}
