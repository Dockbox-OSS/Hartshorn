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
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.dockbox.selene.core.i18n.common.Language
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.server.ServerReference
import org.dockbox.selene.core.util.files.ConfigurateManager
import org.dockbox.selene.core.util.player.PlayerStorageService

abstract class DefaultPlayerStorageService : PlayerStorageService, ServerReference() {

    @ConfigSerializable
    class UserDataModel {

        @Setting
        var language: Language = Selene.getServer().globalConfig.getDefaultLanguage()

    }

    override fun setLanguagePreference(uuid: UUID, lang: Language) {
        val userData = this.getUserData(uuid)
        userData.language = lang
        this.updateUserData(uuid, userData)

    }

    override fun getLanguagePreference(uuid: UUID): Language {
        return getUserData(uuid).language
    }

    private fun updateUserData(uuid: UUID, userData: UserDataModel) {
        val cm = Selene.getInstance(ConfigurateManager::class.java)
        val file = cm.getDataFile(super.getExtension(Selene::class.java)!!, "userdata/$uuid")
        cm.writeFileContent(file, userData)
    }

    private fun getUserData(uuid: UUID): UserDataModel {
        val cm = Selene.getInstance(ConfigurateManager::class.java)
        val file = cm.getDataFile(super.getExtension(Selene::class.java)!!, "userdata/$uuid")
        val userData = cm.getFileContent(file, UserDataModel::class.java)
        return userData.orElse(UserDataModel())
    }
}
