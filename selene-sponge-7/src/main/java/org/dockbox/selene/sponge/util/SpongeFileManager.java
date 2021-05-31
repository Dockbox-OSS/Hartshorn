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

package org.dockbox.selene.sponge.util;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.persistence.DefaultAbstractFileManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.nio.file.Path;

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public class SpongeFileManager extends DefaultAbstractFileManager {

    @NotNull
    public Path getDataDir() {
        return this.getServerRoot().resolve("data/");
    }

    @NotNull
    public Path getServerRoot() {
        return Sponge.getGame().getGameDirectory();
    }

    @NotNull
    public Path getLogsDir() {
        return this.getServerRoot().resolve("logs/");
    }

    @NotNull
    public Exceptional<Path> getModDir() {
        return Exceptional.of(this.createPathIfNotExists(this.getServerRoot().resolve("mods/")));
    }

    @NotNull
    public Path getPluginDir() {
        return this.createPathIfNotExists(this.getServerRoot().resolve("plugins/"));
    }

    @NotNull
    public Path getServiceConfigsDir() {
        return this.getServerRoot().resolve("config/services/");
    }

    @NotNull
    public Exceptional<Path> getModdedPlatformModsConfigDir() {
        return Exceptional.of(this.getServerRoot().resolve("config/"));
    }

    @NotNull
    public Path getPlatformPluginsConfigDir() {
        return this.getServerRoot().resolve("config/plugins/");
    }
}