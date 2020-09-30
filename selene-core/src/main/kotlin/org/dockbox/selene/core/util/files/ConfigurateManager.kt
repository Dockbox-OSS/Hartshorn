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

/**
 * Low-level interface defining functionality to interact with Configurate based file types. While this type encourages
 * the usage of Configurate based instances, it is possible to create implementations for alternative configuration
 * libraries and/or frameworks.
 *
 * @constructor Create the manager with a given [FileType]
 */
abstract class ConfigurateManager(val fileType: FileType) {

    /**
     * Gets the default data file for a given [Extension]. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @param extension The [Extension] providing identification
     * @return A [Path] reference to a file
     */
    abstract fun getDataFile(extension: Extension): Path

    /**
     * Gets the default config file for a given [Extension]. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @param extension The [Extension] providing identification
     * @return A [Path] reference to a file
     */
    abstract fun getConfigFile(extension: Extension): Path

    /**
     * Gets a specific data file for a given [Extension]. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @param extension The [Extension] providing identification
     * @param file The name of the lookup file
     * @return A [Path] reference to a file
     */
    abstract fun getDataFile(extension: Extension, file: String): Path

    /**
     * Gets a specific config file for a given [Extension]. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @param extension The [Extension] providing identification
     * @param file The name of the lookup file
     * @return A [Path] reference to a file
     */
    abstract fun getConfigFile(extension: Extension, file: String): Path

    /**
     * Get the content of a file, and map the given values to a generic type [T]. The exact file is completely
     * dynamic, though it is usually encouraged to use [ConfigurateManager.getDataFile] or
     * [ConfigurateManager.getConfigFile] to obtain appropriate files.
     *
     * @param T The type parameter of the type to map to
     * @param file The file to read the content of
     * @param type The exact type to map to
     * @return A [Exceptional] instance holding either the mapping [T] instance, or a [Throwable]
     */
    abstract fun <T> getFileContent(file: Path, type: Class<T>): Exceptional<T>

    /**
     * Write a generic type [T] to a given file. The exact file is completely
     * dynamic, though it is usually encouraged to use [ConfigurateManager.getDataFile] or
     * [ConfigurateManager.getConfigFile] to obtain appropriate files.
     *
     * @param T The type parameter of the content
     * @param file The file to write the content of
     * @param content The content to write
     * @return A [Exceptional] instance holding a [Boolean] indicating the success status of the write process, or a
     * [Throwable]. If a [Throwable] is present, the [Boolean] value should be false.
     */
    abstract fun <T> writeFileContent(file: Path, content: T): Exceptional<Boolean>

    /**
     * Get the base data directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A [Path] reference to a directory
     */
    abstract fun getDataDir(): Path

    /**
     * Get the base log directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A [Path] reference to a directory
     */
    abstract fun getLogsDir(): Path

    /**
     * Get the base directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A [Path] reference to a directory
     */
    abstract fun getServerRoot(): Path

    /**
     * Get the base extensions directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A [Path] reference to a directory
     */
    abstract fun getExtensionDir(): Path

    /**
     * Get the base mods directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * Depending on the platform this directory may not be present.
     *
     * @return A [Optional] object containing either a [Path] reference to a directory, or nothing.
     */
    abstract fun getModDir(): Optional<Path>

    /**
     * Get the base plugin directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A [Path] reference to a directory
     */
    abstract fun getPluginDir(): Path

    /**
     * Get the configuration folder for extensions directory of a platform file system. The exact location is decided
     * by the top-level implementation of this type.
     *
     * @return A [Path] reference to a directory
     */
    abstract fun getExtensionConfigsDir(): Path

    /**
     * Get the configuration folder for extensions directory of a platform file system. The exact location is decided
     * by the top-level implementation of this type.
     *
     * Depending on the platform this directory may not be present.
     *
     * @return A [Optional] object containing either a [Path] reference to a directory, or nothing.
     */
    abstract fun getModdedPlatformModsConfigDir(): Optional<Path>

    /**
     * Get the configuration folder for extensions directory of a platform file system. The exact location is decided
     * by the top-level implementation of this type.
     *
     * @return A [Path] reference to a directory
     */
    abstract fun getPlatformPluginsConfigDir(): Path

    /**
     * Evaluates whether or not a given [Path] reference directory exists. If it exists nothing is done. If it did not
     * yet exist, the directory is created.
     *
     * @param path The [Path] to evaluate
     * @return The created [Path]
     */
    abstract fun createPathIfNotExists(path: Path): Path

    /**
     * Evaluates whether or not a given [Path] reference file exists. If it exists nothing is done. If it did not yet
     * exist, the directory is created.
     *
     * @param file The [Path] to evaluate
     * @return The created [Path]
     */
    abstract fun createFileIfNotExists(file: Path): Path



}
