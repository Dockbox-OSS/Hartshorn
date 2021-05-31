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

package org.dockbox.selene.persistence;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.persistence.mapping.GenericType;

import java.nio.file.Path;

/**
 * Low-level interface defining functionality to interact with Configurate based file types. While
 * this type encourages the usage of Configurate based instances, it is possible to create
 * implementations for alternative configuration libraries and/or frameworks.
 */
public interface FileManager extends InjectableType {

    /**
     * Gets the default data file for a given {@link TypedOwner}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param owner
     *         The {@link TypedOwner} providing identification
     *
     * @return A {@link Path} reference to a file
     */
    Path getDataFile(TypedOwner owner);

    default Path getConfigFile(Class<?> owner) {
        return this.getConfigFile(this.owner(owner));
    }

    /**
     * Gets the default config file for a given {@link TypedOwner}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param owner
     *         The {@link TypedOwner} providing identification
     *
     * @return A {@link Path} reference to a file
     */
    Path getConfigFile(TypedOwner owner);

    default Path getDataFile(Class<?> owner, String file) {
        return this.getDataFile(this.owner(owner), file);
    }

    /**
     * Gets a specific data file for a given {@link TypedOwner}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param owner
     *         The {@link TypedOwner} providing identification
     * @param file
     *         The name of the lookup file
     *
     * @return A {@link Path} reference to a file
     */
    Path getDataFile(TypedOwner owner, String file);

    default Path getConfigFile(Class<?> owner, String file) {
        return this.getConfigFile(this.owner(owner), file);
    }

    /**
     * Gets a specific config file for a given {@link TypedOwner}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param owner
     *         The {@link TypedOwner} providing identification
     * @param file
     *         The name of the lookup file
     *
     * @return A {@link Path} reference to a file
     */
    Path getConfigFile(TypedOwner owner, String file);

    /**
     * Get the content of a file, and map the given values to a generic type {@code T}. The exact file
     * is completely dynamic, though it is usually encouraged to use {@link FileManager#getDataFile}
     * or {@link FileManager#getConfigFile} to obtain appropriate files.
     *
     * @param <T>
     *         The type parameter of the type to map to
     * @param file
     *         The file to read the content of
     * @param type
     *         The exact type to map to
     *
     * @return A {@link Exceptional} instance holding either the mapping {@code T} instance, or a
     *         {@link Throwable}
     */
    <T> Exceptional<T> read(Path file, Class<T> type);
    <T> Exceptional<T> read(Path file, GenericType<T> type);

    /**
     * Write a generic type {@code T} to a given file. The exact file is completely dynamic, though it
     * is usually encouraged to use {@link FileManager#getDataFile} or {@link
     * FileManager#getConfigFile} to obtain appropriate files.
     *
     * @param <T>
     *         The type parameter of the content
     * @param file
     *         The file to write the content of
     * @param content
     *         The content to write
     *
     * @return A {@link Exceptional} instance holding a {@link Boolean} indicating the success status
     *         of the write process, or a {@link Throwable}. If a {@link Throwable} is present, the {@link
     *         Boolean} value should be false.
     */
    <T> Exceptional<Boolean> write(Path file, T content);

    default Path getDataDir(Class<?> owner) {
        return this.getDataDir(this.owner(owner));
    }

    /**
     * Get the data directory for a given {@link TypedOwner}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param owner
     *         The {@link TypedOwner} providing identification
     *
     * @return A {@link Path} reference to the data directory
     */
    default Path getDataDir(TypedOwner owner) {
        return this.getDataDir().resolve(owner.id());
    }

    /**
     * Get the base data directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    Path getDataDir();

    /**
     * Get the base log directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    Path getLogsDir();

    /**
     * Get the base directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    Path getServerRoot();

    /**
     * Get the base mods directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * <p>Depending on the platform this directory may not be present.
     *
     * @return A {@link Exceptional} object containing either a {@link Path} reference to a directory,
     *         or nothing.
     */
    Exceptional<Path> getModDir();

    /**
     * Get the base plugin directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    Path getPluginDir();

    default Path getServiceConfigDir(Class<?> owner) {
        return this.getServiceConfigDir(this.owner(owner));
    }

    /**
     * Get the configuration directory for a given {@link TypedOwner}. The exact location is decided by
     * the top-level implementation of this type.
     *
     * @param owner
     *         The {@link TypedOwner} providing identification
     *
     * @return A {@link Path} reference to the configuration directory
     */
    default Path getServiceConfigDir(TypedOwner owner) {
        return this.getServiceConfigsDir().resolve(owner.id());
    }

    /**
     * Get the configuration folder for owners directory of a platform file system. The exact
     * location is decided by the top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    Path getServiceConfigsDir();

    /**
     * Get the configuration folder for owners directory of a platform file system. The exact
     * location is decided by the top-level implementation of this type.
     *
     * <p>Depending on the platform this directory may not be present.
     *
     * @return A {@link Exceptional} object containing either a {@link Path} reference to a directory,
     *         or nothing.
     */
    Exceptional<Path> getModdedPlatformModsConfigDir();

    /**
     * Get the configuration folder for owners directory of a platform file system. The exact
     * location is decided by the top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    Path getPlatformPluginsConfigDir();

    /**
     * Evaluates whether or not a given {@link Path} reference directory exists. If it exists nothing
     * is done. If it did not yet exist, the directory is created.
     *
     * @param path
     *         The {@link Path} to evaluate
     *
     * @return The created {@link Path}
     */
    Path createPathIfNotExists(Path path);

    /**
     * Evaluates whether or not a given {@link Path} reference file exists. If it exists nothing is
     * done. If it did not yet exist, the directory is created.
     *
     * @param file
     *         The {@link Path} to evaluate
     *
     * @return The created {@link Path}
     */
    Path createFileIfNotExists(Path file);

    /**
     * Attempts to move a file to a target file. If the target file does not exist it will be created.
     *
     * @param sourceFile
     *         The original file to move
     * @param targetFile
     *         The target location of the file (fully qualified)
     *
     * @return true if the file was moved successfully, otherwise false
     */
    boolean move(Path sourceFile, Path targetFile);

    /**
     * Attempts to copy a file to a target file. If the target file does not exist it will be created.
     *
     * @param sourceFile
     *         The original file to copy
     * @param targetFile
     *         The target location of the file (fully qualified)
     *
     * @return true if the file was moved successfully, otherwise false
     */
    boolean copy(Path sourceFile, Path targetFile);

    /**
     * Attempts to copy a pre-made resource to a target file. If the target file already exists
     * nothing is done.
     *
     * @param defaultFileName
     *         The name of the resource to copy
     * @param targetFile
     *         The target location of the file (fully qualified)
     *
     * @return true if the file was copied, otherwise false
     */
    boolean copyDefaultFile(String defaultFileName, Path targetFile);

    default TypedOwner owner(Class<?> type) {
        return Selene.context().get(OwnerLookup.class).lookup(type);
    }
}
