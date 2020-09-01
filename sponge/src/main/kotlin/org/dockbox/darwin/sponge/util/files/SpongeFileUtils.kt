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

package org.dockbox.darwin.sponge.util.files

import org.dockbox.darwin.core.util.files.FileUtils
import org.spongepowered.api.Sponge
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class SpongeFileUtils : FileUtils {
    override fun createPathIfNotExists(path: Path): Path {
        if (!path.toFile().exists()) path.toFile().mkdirs()
        return path
    }

    override fun createFileIfNotExists(file: Path): Path {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.parent)
                Files.createFile(file)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return file
    }

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

    override fun getModuleDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("modules/"))
    }

    override fun getModDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("mods/"))
    }

    override fun getPluginDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("plugins/"))
    }

    override fun getModuleConfigDir(): Path {
        return getServerRoot().resolve("config/modules/")
    }

    override fun getModConfigDir(): Path {
        return getServerRoot().resolve("config/")
    }

    override fun getPluginConfigDir(): Path {
        return getServerRoot().resolve("config/plugins/")
    }
}
