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

package org.dockbox.selene.minecraft.players;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.entity.annotations.Metadata;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.persistence.FileManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.UUID;

public abstract class DefaultPlayers implements Players {

    @Override
    public void setLanguagePreference(@NotNull UUID uuid, @NotNull Language lang) {
        UserDataModel userData = DefaultPlayers.getUserData(uuid);
        userData.language = lang;
        DefaultPlayers.updateUserData(uuid, userData);
    }

    @NotNull
    @Override
    public Language getLanguagePreference(@NotNull UUID uuid) {
        return DefaultPlayers.getUserData(uuid).language;
    }

    private static UserDataModel getUserData(UUID uuid) {
        FileManager cm = Selene.provide(FileManager.class);
        Path file = cm.getDataFile(Selene.class, "userdata/" + uuid);
        Exceptional<UserDataModel> userDataModel = cm.read(file, UserDataModel.class);
        return userDataModel.or(new UserDataModel());
    }

    private static void updateUserData(UUID uuid, UserDataModel userData) {
        FileManager cm = Selene.provide(FileManager.class);
        Path file = cm.getDataFile(Selene.class, "userdata/" + uuid);
        cm.write(file, userData);
    }

    @Metadata(alias = "userdata")
    private static class UserDataModel {
        private Language language = Selene.getServer().getGlobalConfig().getDefaultLanguage();
    }
}
