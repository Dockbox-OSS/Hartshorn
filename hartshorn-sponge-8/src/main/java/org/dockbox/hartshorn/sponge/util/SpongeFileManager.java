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

package org.dockbox.hartshorn.sponge.util;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.persistence.DefaultAbstractFileManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.nio.file.Path;

public class SpongeFileManager extends DefaultAbstractFileManager {

    @NotNull
    public Path data() {
        return this.root().resolve("data/");
    }

    @NotNull
    public Path root() {
        return Sponge.game().gameDirectory();
    }

    @NotNull
    public Path logs() {
        return this.root().resolve("logs/");
    }

    @NotNull
    public Exceptional<Path> mods() {
        return Exceptional.of(this.createPathIfNotExists(this.root().resolve("mods/")));
    }

    @NotNull
    public Path plugins() {
        return this.createPathIfNotExists(this.root().resolve("plugins/"));
    }

    @NotNull
    public Path serviceConfigs() {
        return this.root().resolve("config/services/");
    }

    @NotNull
    public Exceptional<Path> modConfigs() {
        return Exceptional.of(this.root().resolve("config/"));
    }

    @NotNull
    public Path pluginConfigs() {
        return this.root().resolve("config/plugins/");
    }
}
