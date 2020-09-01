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

interface ConfigManager {

    fun getConfigDir(module: Class<*>): Path
    fun getConfigDir(module: Any): Path

    fun getConfigFile(module: Class<*>): Path
    fun getConfigFile(module: Any): Path

    fun getConfigContents(module: Class<*>): Map<String, Any>
    fun getConfigContents(module: Any): Map<String, Any>

    fun <T> getConfigContents(module: Class<*>, convertTo: Class<T>, defaultValue: T): T
    fun <T> getConfigContents(module: Any, convertTo: Class<T>, defaultValue: T): T

    fun <T> writeToConfig(module: Class<*>, data: T)
    fun <T> writeToConfig(module: Any, data: T)


}
