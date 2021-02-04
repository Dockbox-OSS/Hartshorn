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

package org.dockbox.selene.core.impl;

import org.dockbox.selene.core.PlayerStorageService;
import org.dockbox.selene.core.annotations.entity.Metadata;
import org.dockbox.selene.core.files.FileManager;
import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.Selene;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.UUID;

public abstract class DefaultPlayerStorageService implements PlayerStorageService
{

    @Override
    public void setLanguagePreference(@NotNull UUID uuid, @NotNull Language lang)
    {
        UserDataModel userData = this.getUserData(uuid);
        userData.language = lang;
        this.updateUserData(uuid, userData);
    }

    @NotNull
    @Override
    public Language getLanguagePreference(@NotNull UUID uuid)
    {
        return this.getUserData(uuid).language;
    }

    @SuppressWarnings("ConstantConditions")
    private UserDataModel getUserData(UUID uuid)
    {
        FileManager cm = Selene.provide(FileManager.class);
        Path file = cm.getDataFile(Selene.class, "userdata/" + uuid);
        Exceptional<UserDataModel> userDataModel = cm.read(file, UserDataModel.class);
        return userDataModel.orElse(new UserDataModel());
    }

    @SuppressWarnings("ConstantConditions")
    private void updateUserData(UUID uuid, UserDataModel userData)
    {
        FileManager cm = Selene.provide(FileManager.class);
        Path file = cm.getDataFile(Selene.class, "userdata/" + uuid);
        cm.write(file, userData);
    }

    @Metadata(alias = "userdata")
    private static class UserDataModel
    {
        private Language language = Selene.getServer().getGlobalConfig().getDefaultLanguage();
    }

}
