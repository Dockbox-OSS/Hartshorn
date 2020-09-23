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

package org.dockbox.selene.core.util.files

import org.dockbox.selene.core.server.ServerReference
import org.dockbox.selene.core.util.extension.Extension
import java.nio.file.Path
import java.util.*

abstract class DataManager : ServerReference() {

    open fun getDataDir(extension: Class<*>): Path {
        return this.runWithExtension<Any>(extension) { annotation ->
            this.getInstance(FileUtils::class.java).getDataDir().resolve(annotation.id)
        } as Path
    }

    open fun getDataDir(extension: Any): Path {
        var shadow: Any = extension
        if ((shadow !is Class<*>)) shadow = shadow.javaClass
        return this.getDataDir(shadow as Class<*>)
    }

    open fun getDefaultDataFile(module: Class<*>): Path {
        return runWithExtension(module) { annotation: Extension ->
            val dataPath = this.getDataDir(module)
            getInstance(FileUtils::class.java).createFileIfNotExists(getFileType().asPath(dataPath, annotation.id))
        }
    }
    open fun getDefaultDataFile(extension: Any): Path {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        return this.getDefaultDataFile((shadow as Class<*>))
    }

    @Suppress("UNCHECKED_CAST")
    open fun getDefaultDataFileContents(module: Class<*>): Map<String, Any> {
        return this.getDefaultDataFileContents(module, MutableMap::class.java, HashMap<Any, Any>()) as Map<String, Any>
    }

    @Suppress("UNCHECKED_CAST")
    open fun getDefaultDataFileContents(module: Any): Map<String, Any> {
        return this.getDefaultDataFileContents(module as Class<*>, MutableMap::class.java, HashMap<String, Any>()) as Map<String, Any>
    }

    abstract fun getDataFileContents(extension: Class<*>, fileName: String): Map<String, Any>
    open fun getDataFileContents(extension: Any, fileName: String): Map<String, Any> {
        return this.getDataFileContents(extension.javaClass, fileName)
    }

    abstract fun <T> getDefaultDataFileContents(extension: Class<*>, convertTo: Class<T>, defaultValue: T): T
    open fun <T> getDefaultDataFileContents(extension: Any, convertTo: Class<T>, defaultValue: T): T {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        return this.getDefaultDataFileContents((shadow as Class<*>), convertTo, defaultValue)
    }

    abstract fun <T> writeToDefaultDataFile(extension: Class<*>, data: T)
    open fun <T> writeToDefaultDataFile(extension: Any, data: T) {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        this.writeToDefaultDataFile((shadow as Class<*>), data)
    }

    abstract fun <T> writeToDataFile(extension: Class<*>, data: T, fileName: String)
    open fun <T> writeToDataFile(extension: Any, data: T, fileName: String) {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        this.writeToDataFile((shadow as Class<*>), data, fileName)
    }

    abstract fun getFileType(): FileType
}
