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

import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.server.ServerReference
import org.dockbox.darwin.core.util.extension.Extension
import java.nio.file.Path
import java.util.*

abstract class ConfigManager : ServerReference() {

    open fun getConfigDir(extension: Class<*>): Path {
        return runWithExtension(extension) { annotation: Extension ->
            getFileType().asPath(
                    Server.getInstance(FileUtils::class.java).getModuleConfigDir(),
                    annotation.id
            )
        }
    }

    open fun getConfigDir(extension: Any): Path {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        return this.getConfigDir(shadow as Class<*>)
    }

    open fun getConfigFile(extension: Class<*>): Path {
        return runWithExtension(extension) { annotation: Extension ->
            val configPath = this.getConfigDir(extension)
            Server.getInstance(FileUtils::class.java).createFileIfNotExists(getFileType().asPath(configPath, annotation.id))
        }
    }

    open fun getConfigFile(extension: Any): Path {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        return this.getConfigFile(shadow as Class<*>)
    }

    @Suppress("UNCHECKED_CAST")
    open fun getConfigContents(extension: Class<*>): Map<String, Any> {
        return this.getConfigContents(extension, MutableMap::class.java, HashMap<Any, Any>()) as Map<String, Any>
    }

    @Suppress("UNCHECKED_CAST")
    open fun getConfigContents(extension: Any): Map<String, Any> {
        return this.getConfigContents(extension, MutableMap::class.java, HashMap<Any, Any>()) as Map<String, Any>
    }

    abstract fun <T> getConfigContents(extension: Class<*>, convertTo: Class<T>, defaultValue: T): T
    open fun <T> getConfigContents(extension: Any, convertTo: Class<T>, defaultValue: T): T {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        return this.getConfigContents((shadow as Class<*>), convertTo, defaultValue)
    }

    abstract fun <T> writeToConfig(extension: Class<*>, data: T)
    open fun <T> writeToConfig(extension: Any, data: T) {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        this.writeToConfig((shadow as Class<*>), data)
    }

    abstract fun getFileType(): FileType

}
