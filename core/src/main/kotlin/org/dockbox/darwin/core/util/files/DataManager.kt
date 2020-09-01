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

interface DataManager<D> {

    fun getDataDir(module: Class<*>): Path
    fun getDataDir(module: Any): Path

    fun getDefaultDataFile(module: Class<*>): File
    fun getDefaultDataFile(module: Any): File

    fun getDefaultBulkDataFile(module: Class<*>): File
    fun getDefaultBulkDataFile(module: Any): File

    fun getDataContents(module: Class<*>): Map<String, Any>
    fun getDataContents(module: Any): Map<String, Any>
    fun getDataContents(module: Class<*>, fileName: String): Map<String, Any>
    fun getDataContents(module: Any, fileName: String): Map<String, Any>

    fun <T> getDataContents(module: Class<*>, convertTo: Class<T>, defaultValue: T): T
    fun <T> getDataContents(module: Any, convertTo: Class<T>, defaultValue: T): T

    fun <T> writeToData(module: Class<*>, data: T)
    fun <T> writeToData(module: Any, data: T)
    fun <T> writeToData(module: Class<*>, data: T, fileName: String)
    fun <T> writeToData(module: Any, data: T, fileName: String)

    fun getBulkDao(module: Any, type: Class<*>, fileName: String): D
    fun getBulkDao(module: Class<*>, type: Class<*>, fileName: String): D
    fun getDefaultBulkDao(module: Any, type: Class<*>): D
    fun getDefaultBulkDao(module: Class<*>, type: Class<*>): D

}
