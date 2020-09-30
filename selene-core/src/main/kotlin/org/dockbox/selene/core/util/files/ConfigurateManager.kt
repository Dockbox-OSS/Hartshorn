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

package org.dockbox.selene.core.util.files

import java.nio.file.Path
import java.util.*
import org.dockbox.selene.core.objects.optional.Exceptional
import org.dockbox.selene.core.util.extension.Extension

abstract class ConfigurateManager {

    abstract fun getDataFile(extension: Extension): Path
    abstract fun getConfigFile(extension: Extension): Path

    abstract fun getDataFile(extension: Extension, file: String): Path
    abstract fun getConfigFile(extension: Extension, file: String): Path

    abstract fun getPropertyFileContent(file: Path): Exceptional<Properties>
    abstract fun <T> getFileContent(file: Path, type: Class<T>): Exceptional<T>
    abstract fun <T> writeFileContent(file: Path, content: T): Exceptional<Boolean>

    abstract fun getDataDir(): Path
    abstract fun getLogsDir(): Path
    abstract fun getServerRoot(): Path

    abstract fun getExtensionDir(): Path
    abstract fun getModDir(): Path
    abstract fun getPluginDir(): Path

    abstract fun getExtensionConfigsDir(): Path
    abstract fun getModdedPlatformModsConfigDir(): Optional<Path>
    abstract fun getPlatformPluginsConfigDir(): Path

    abstract fun createPathIfNotExists(path: Path): Path
    abstract fun createFileIfNotExists(file: Path): Path

}
