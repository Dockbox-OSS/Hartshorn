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
import org.jetbrains.annotations.NotNull
import java.nio.file.Path
import java.util.*

abstract class BulkDataManager<D> : ServerReference() {

    open fun getDataDir(extension: Class<*>): Path {
        return runWithExtension(extension) { annotation: Extension -> getInstance(FileUtils::class.java).getDataDir().resolve(annotation.id) }
    }

    open fun getDataDir(extension: Any): Path {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        return this.getDataDir(shadow as Class<*>)
    }

    open fun getDefaultBulkDataFile(extension: Class<*>): Path {
        return runWithExtension(extension) { annotation: Extension ->
            val dataPath = this.getDataDir(extension)
            getInstance(FileUtils::class.java).createFileIfNotExists(getFileType().asPath(dataPath, annotation.id))
        }
    }

    open fun getDefaultBulkDataFile(extension: Any): Path {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        return this.getDefaultBulkDataFile(shadow as Class<*>)
    }

    open fun getBulkDao(extension: Class<*>, type: Class<*>, fileName: String): Optional<D> {
        return runWithExtension(extension) {
            val dataDir = this.getDataDir(extension)
            this.getBulkDao(type, getFileType().asPath(dataDir, fileName))
        }
    }

    open fun getBulkDao(extension: Any, type: Class<*>, fileName: String): Optional<D> {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        return this.getBulkDao(shadow as Class<*>, type, fileName)
    }

    open fun getDefaultBulkDao(extension: Class<*>, type: Class<*>): Optional<D> {
        return this.getBulkDao(type, this.getDefaultBulkDataFile(extension))
    }

    open fun getDefaultBulkDao(extension: Any, type: Class<*>): Optional<D> {
        var shadow = extension
        if (shadow !is Class<*>) shadow = shadow.javaClass
        return this.getBulkDao(type, this.getDefaultBulkDataFile(shadow))
    }
    abstract fun getBulkDao(extension: @NotNull Class<*>, file: @NotNull Path): Optional<D>

    abstract fun getFileType(): FileType
}
