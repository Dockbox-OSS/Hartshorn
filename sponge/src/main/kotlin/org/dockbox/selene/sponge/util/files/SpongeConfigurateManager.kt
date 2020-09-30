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

package org.dockbox.selene.sponge.util.files

import java.nio.file.Path
import java.util.*
import org.dockbox.selene.core.impl.util.files.DefaultConfigurateManager
import org.dockbox.selene.core.util.files.FileType
import org.spongepowered.api.Sponge

class SpongeFileUtils : DefaultConfigurateManager(FileType.YAML) {

    override fun getDataDir(): Path {
        return getServerRoot().resolve("data/")
    }

    override fun getLogsDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("logs/"))
    }

    override fun getServerRoot(): Path {
        val modDir = Sponge.getGame().gameDirectory.toAbsolutePath()
        return createPathIfNotExists(modDir)
    }

    override fun getExtensionDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("extensions/"))
    }

    override fun getModDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("mods/"))
    }

    override fun getPluginDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("plugins/"))
    }

    override fun getExtensionConfigsDir(): Path {
        return getServerRoot().resolve("config/extensions/")
    }

    override fun getModdedPlatformModsConfigDir(): Optional<Path> {
        return Optional.of(getServerRoot().resolve("config/"))
    }

    override fun getPlatformPluginsConfigDir(): Path {
        return getServerRoot().resolve("config/plugins/")
    }
}
