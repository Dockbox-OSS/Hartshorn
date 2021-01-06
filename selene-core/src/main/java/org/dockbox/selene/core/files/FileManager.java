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

package org.dockbox.selene.core.files;

import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.util.SeleneUtils;

import java.nio.file.Path;

/**
 * Low-level interface defining functionality to interact with Configurate based file types. While this type encourages
 * the usage of Configurate based instances, it is possible to create implementations for alternative configuration
 * libraries and/or frameworks.
 */
public abstract class FileManager {

    private final FileType fileType;

    protected FileManager(FileType fileType) {
        this.fileType = fileType;
    }

    public FileType getFileType() {
        return this.fileType;
    }

    /**
     * Gets the default data file for a given {@link Extension}. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @param extension
     *         The {@link Extension} providing identification
     *
     * @return A {@link Path} reference to a file
     */
    public abstract Path getDataFile(Extension extension);
    public Path getDataFile(Class<?> extension) {
        return this.getDataFile(SeleneUtils.REFLECTION.getExtension(extension));
    }

    /**
     * Gets the default config file for a given {@link Extension}. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @param extension
     *         The {@link Extension} providing identification
     *
     * @return A {@link Path} reference to a file
     */
    public abstract Path getConfigFile(Extension extension);
    public Path getConfigFile(Class<?> extension) {
        return this.getConfigFile(SeleneUtils.REFLECTION.getExtension(extension));
    }

    /**
     * Gets a specific data file for a given {@link Extension}. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @param extension
     *         The {@link Extension} providing identification
     * @param file
     *         The name of the lookup file
     *
     * @return A {@link Path} reference to a file
     */
    public abstract Path getDataFile(Extension extension, String file);
    public Path getDataFile(Class<?> extension, String file) {
        return this.getDataFile(SeleneUtils.REFLECTION.getExtension(extension), file);
    }

    /**
     * Gets a specific config file for a given {@link Extension}. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @param extension
     *         The {@link Extension} providing identification
     * @param file
     *         The name of the lookup file
     *
     * @return A {@link Path} reference to a file
     */
    public abstract Path getConfigFile(Extension extension, String file);
    public Path getConfigFile(Class<?> extension, String file) {
        return this.getConfigFile(SeleneUtils.REFLECTION.getExtension(extension), file);
    }

    /**
     * Get the content of a file, and map the given values to a generic type {@link T}. The exact file is completely
     * dynamic, though it is usually encouraged to use {@link FileManager#getDataFile} or
     * {@link FileManager#getConfigFile} to obtain appropriate files.
     *
     * @param <T>
     *         The type parameter of the type to map to
     * @param file
     *         The file to read the content of
     * @param type
     *         The exact type to map to
     *
     * @return A {@link Exceptional} instance holding either the mapping {@link T} instance, or a {@link Throwable}
     */
    public abstract <T> Exceptional<T> getFileContent(Path file, Class<T> type);

    /**
     * Write a generic type {@link T} to a given file. The exact file is completely
     * dynamic, though it is usually encouraged to use {@link FileManager#getDataFile} or
     * {@link FileManager#getConfigFile} to obtain appropriate files.
     *
     * @param <T>
     *         The type parameter of the content
     * @param file
     *         The file to write the content of
     * @param content
     *         The content to write
     *
     * @return A {@link Exceptional} instance holding a {@link Boolean} indicating the success status of the write process, or a
     *         {@link Throwable}. If a {@link Throwable} is present, the {@link Boolean} value should be false.
     */
    public abstract <T> Exceptional<Boolean> writeFileContent(Path file, T content);

    /**
     * Get the data directory for a given {@link Extension}. The exact location is decided by the top-level implementation
     * of this type.
     *
     * @param extension
     *         The {@link Extension} providing identification
     *
     * @return A {@link Path} reference to the data directory
     */
    public Path getDataDir(Extension extension) {
        return this.getDataDir().resolve(extension.id());
    }
    public Path getDataDir(Class<?> extension) {
        return this.getDataDir(SeleneUtils.REFLECTION.getExtension(extension));
    }

    /**
     * Get the base data directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getDataDir();

    /**
     * Get the base log directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getLogsDir();

    /**
     * Get the base directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getServerRoot();

    /**
     * Get the base extensions directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getExtensionDir();

    /**
     * Get the base mods directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     * <p>
     * Depending on the platform this directory may not be present.
     *
     * @return A {@link Exceptional} object containing either a {@link Path} reference to a directory, or nothing.
     */
    public abstract Exceptional<Path> getModDir();

    /**
     * Get the base plugin directory of a platform file system. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getPluginDir();

    /**
     * Get the configuration directory for a given {@link Extension}. The exact location is decided by the top-level
     * implementation of this type.
     *
     * @param extension
     *         The {@link Extension} providing identification
     *
     * @return A {@link Path} reference to the configuration directory
     */
    public Path getExtensionConfigDir(Extension extension) {
        return this.getExtensionConfigsDir().resolve(extension.id());
    }
    public Path getExtensionConfigDir(Class<?> extension) {
        return this.getExtensionConfigDir(SeleneUtils.REFLECTION.getExtension(extension));
    }

    /**
     * Get the configuration folder for extensions directory of a platform file system. The exact location is decided
     * by the top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getExtensionConfigsDir();

    /**
     * Get the configuration folder for extensions directory of a platform file system. The exact location is decided
     * by the top-level implementation of this type.
     * <p>
     * Depending on the platform this directory may not be present.
     *
     * @return A {@link Exceptional} object containing either a {@link Path} reference to a directory, or nothing.
     */
    public abstract Exceptional<Path> getModdedPlatformModsConfigDir();

    /**
     * Get the configuration folder for extensions directory of a platform file system. The exact location is decided
     * by the top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getPlatformPluginsConfigDir();

    /**
     * Evaluates whether or not a given {@link Path} reference directory exists. If it exists nothing is done. If it did not
     * yet exist, the directory is created.
     *
     * @param path
     *         The {@link Path} to evaluate
     *
     * @return The created {@link Path}
     */
    public abstract Path createPathIfNotExists(Path path);

    /**
     * Evaluates whether or not a given {@link Path} reference file exists. If it exists nothing is done. If it did not yet
     * exist, the directory is created.
     *
     * @param file
     *         The {@link Path} to evaluate
     *
     * @return The created {@link Path}
     */
    public abstract Path createFileIfNotExists(Path file);

    /**
     * Attempts to move a file to a target file. If the target file does not exist it will be created.
     *
     * @param sourceFile The original file to move
     * @param targetFile The target location of the file (fully qualified)
     * @return true if the file was moved successfully, otherwise false
     */
    public abstract boolean move(Path sourceFile, Path targetFile);

    /**
     * Attempts to copy a pre-made resource to a target file. If the target file already exists nothing is done.
     *
     * @param defaultFileName The name of the resource to copy
     * @param targetFile The target location of the file (fully qualified)
     * @return true if the file was copied, otherwise false
     */
    public abstract boolean copyDefaultFile(String defaultFileName, Path targetFile);
}
