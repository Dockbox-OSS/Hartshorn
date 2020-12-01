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

package org.dockbox.selene.sponge.util;

import org.dockbox.selene.core.impl.files.DefaultConfigurateManager;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.util.files.ConfigurateManager;
import org.dockbox.selene.core.util.files.FileType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.nio.file.Path;

import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

public class SpongeConfigurateManager extends DefaultConfigurateManager {

    /**
     Provides the given {@link FileType} to the super type {@link ConfigurateManager}. And registers any custom
     {@link TypeSerializer} types to
     {@link TypeSerializers#getDefaultSerializers()}.@param fileType
     The file type to be used when mapping.
     */
    protected SpongeConfigurateManager() {
        super(FileType.YAML);
    }

    @NotNull
    @Override
    public Path getDataDir() {
        return getServerRoot().resolve("data/");
    }

    @NotNull
    @Override
    public Path getLogsDir() {
        return getServerRoot().resolve("logs/");
    }

    @NotNull
    @Override
    public Path getServerRoot() {
        return Sponge.getGame().getGameDirectory();
    }

    @NotNull
    @Override
    public Path getExtensionDir() {
        return createPathIfNotExists(getServerRoot().resolve("extensions/"));
    }

    @NotNull
    @Override
    public Exceptional<Path> getModDir() {
        return Exceptional.of(createPathIfNotExists(getServerRoot().resolve("mods/")));
    }

    @NotNull
    @Override
    public Path getPluginDir() {
        return createPathIfNotExists(getServerRoot().resolve("plugins/"));
    }

    @NotNull
    @Override
    public Path getExtensionConfigsDir() {
        return getServerRoot().resolve("config/extensions/");
    }

    @NotNull
    @Override
    public Exceptional<Path> getModdedPlatformModsConfigDir() {
        return Exceptional.of(getServerRoot().resolve("config/"));
    }

    @NotNull
    @Override
    public Path getPlatformPluginsConfigDir() {
        return getServerRoot().resolve("config/plugins/");
    }
}
