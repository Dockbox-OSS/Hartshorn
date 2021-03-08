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

package org.dockbox.selene.api.files;

import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.keys.Keys;
import org.dockbox.selene.api.server.properties.InjectableType;
import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.api.util.Reflect;

import java.nio.file.Path;

/**
 * Low-level interface defining functionality to interact with Configurate based file types. While
 * this type encourages the usage of Configurate based instances, it is possible to create
 * implementations for alternative configuration libraries and/or frameworks.
 */
public abstract class FileManager implements InjectableType {

    private FileType fileType;

    protected FileManager(FileType fileType) {
        this.fileType = fileType;
    }

    public FileType getFileType() {
        return this.fileType;
    }

    protected void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Path getDataFile(Class<?> module) {
        return this.getDataFile(Reflect.getModule(module));
    }

    /**
     * Gets the default data file for a given {@link Module}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param module
     *         The {@link Module} providing identification
     *
     * @return A {@link Path} reference to a file
     */
    public abstract Path getDataFile(Module module);

    public Path getConfigFile(Class<?> module) {
        return this.getConfigFile(Reflect.getModule(module));
    }

    /**
     * Gets the default config file for a given {@link Module}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param module
     *         The {@link Module} providing identification
     *
     * @return A {@link Path} reference to a file
     */
    public abstract Path getConfigFile(Module module);

    public Path getDataFile(Class<?> module, String file) {
        return this.getDataFile(Reflect.getModule(module), file);
    }

    /**
     * Gets a specific data file for a given {@link Module}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param module
     *         The {@link Module} providing identification
     * @param file
     *         The name of the lookup file
     *
     * @return A {@link Path} reference to a file
     */
    public abstract Path getDataFile(Module module, String file);

    public Path getConfigFile(Class<?> module, String file) {
        return this.getConfigFile(Reflect.getModule(module), file);
    }

    /**
     * Gets a specific config file for a given {@link Module}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param module
     *         The {@link Module} providing identification
     * @param file
     *         The name of the lookup file
     *
     * @return A {@link Path} reference to a file
     */
    public abstract Path getConfigFile(Module module, String file);

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
    public abstract <T> Exceptional<T> read(Path file, Class<T> type);

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
    public abstract <T> Exceptional<Boolean> write(Path file, T content);

    public Path getDataDir(Class<?> module) {
        return this.getDataDir(Reflect.getModule(module));
    }

    /**
     * Get the data directory for a given {@link Module}. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @param module
     *         The {@link Module} providing identification
     *
     * @return A {@link Path} reference to the data directory
     */
    public Path getDataDir(Module module) {
        return this.getDataDir().resolve(module.id());
    }

    /**
     * Get the base data directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getDataDir();

    /**
     * Get the base log directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getLogsDir();

    /**
     * Get the base directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getServerRoot();

    /**
     * Get the base modules directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getModuleDir();

    /**
     * Get the base mods directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * <p>Depending on the platform this directory may not be present.
     *
     * @return A {@link Exceptional} object containing either a {@link Path} reference to a directory,
     *         or nothing.
     */
    public abstract Exceptional<Path> getModDir();

    /**
     * Get the base plugin directory of a platform file system. The exact location is decided by the
     * top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getPluginDir();

    public Path getModuleConfigDir(Class<?> module) {
        return this.getModuleConfigDir(Reflect.getModule(module));
    }

    /**
     * Get the configuration directory for a given {@link Module}. The exact location is decided by
     * the top-level implementation of this type.
     *
     * @param module
     *         The {@link Module} providing identification
     *
     * @return A {@link Path} reference to the configuration directory
     */
    public Path getModuleConfigDir(Module module) {
        return this.getModuleConfigsDir().resolve(module.id());
    }

    /**
     * Get the configuration folder for modules directory of a platform file system. The exact
     * location is decided by the top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getModuleConfigsDir();

    /**
     * Get the configuration folder for modules directory of a platform file system. The exact
     * location is decided by the top-level implementation of this type.
     *
     * <p>Depending on the platform this directory may not be present.
     *
     * @return A {@link Exceptional} object containing either a {@link Path} reference to a directory,
     *         or nothing.
     */
    public abstract Exceptional<Path> getModdedPlatformModsConfigDir();

    /**
     * Get the configuration folder for modules directory of a platform file system. The exact
     * location is decided by the top-level implementation of this type.
     *
     * @return A {@link Path} reference to a directory
     */
    public abstract Path getPlatformPluginsConfigDir();

    /**
     * Evaluates whether or not a given {@link Path} reference directory exists. If it exists nothing
     * is done. If it did not yet exist, the directory is created.
     *
     * @param path
     *         The {@link Path} to evaluate
     *
     * @return The created {@link Path}
     */
    public abstract Path createPathIfNotExists(Path path);

    /**
     * Evaluates whether or not a given {@link Path} reference file exists. If it exists nothing is
     * done. If it did not yet exist, the directory is created.
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
     * @param sourceFile
     *         The original file to move
     * @param targetFile
     *         The target location of the file (fully qualified)
     *
     * @return true if the file was moved successfully, otherwise false
     */
    public abstract boolean move(Path sourceFile, Path targetFile);

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
    public abstract boolean copy(Path sourceFile, Path targetFile);

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
    public abstract boolean copyDefaultFile(String defaultFileName, Path targetFile);

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Keys.getPropertyValue(FileTypeProperty.KEY, FileType.class, properties)
                .ifPresent(this::requestFileType);
    }

    public abstract void requestFileType(FileType fileType);
}
