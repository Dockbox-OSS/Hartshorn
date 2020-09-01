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

package org.dockbox.darwin.core.util.files

import java.io.File
import java.nio.file.Path

interface FileUtils {

    fun getDataDir(): Path
    fun getLogsDir(): Path
    fun getServerRoot(): Path

    fun getModuleDir(): Path
    fun getModDir(): Path
    fun getPluginDir(): Path

    fun getModuleConfigDir(): Path
    fun getModConfigDir(): Path
    fun getPluginConfigDir(): Path

    fun createPathIfNotExists(path: Path): Path
    fun createFileIfNotExists(file: Path): Path

}
