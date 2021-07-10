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

package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.persistence.mapping.GenericType;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.persistence.properties.PersistenceProperty;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class DefaultAbstractFileManager implements FileManager {

    private final ObjectMapper mapper;

    protected DefaultAbstractFileManager() {
        this.mapper = Hartshorn.context().get(ObjectMapper.class);
    }

    public FileType getFileType() {
        return this.mapper.getFileType();
    }

    protected void setFileType(FileType fileType) {
        this.mapper.setFileType(fileType);
    }

    public Path getDataFile(Class<?> owner) {
        return this.getDataFile(this.owner(owner));
    }

    @NotNull
    @Override
    public Path getDataFile(@NotNull TypedOwner owner) {
        return this.getDataFile(owner, owner.id());
    }

    @NotNull
    @Override
    public Path getConfigFile(@NotNull TypedOwner owner) {
        return this.getConfigFile(owner, owner.id());
    }

    @NotNull
    @Override
    public Path getDataFile(@NotNull TypedOwner owner, @NotNull String file) {
        return this.createFileIfNotExists(this.getFileType().asPath(this.getDataDir().resolve(owner.id()), file));
    }

    @NotNull
    @Override
    public Path getConfigFile(@NotNull TypedOwner owner, @NotNull String file) {
        return this.createFileIfNotExists(this.getFileType().asPath(this.getServiceConfigsDir().resolve(owner.id()), file));
    }

    @NotNull
    @Override
    public Path createPathIfNotExists(@NotNull Path path) {
        return HartshornUtils.createPathIfNotExists(path);
    }

    @NotNull
    @Override
    public Path createFileIfNotExists(@NotNull Path file) {
        return HartshornUtils.createFileIfNotExists(file);
    }

    @Override
    public boolean move(Path sourceFile, Path targetFile) {
        this.createFileIfNotExists(targetFile);
        try {
            Files.move(sourceFile, targetFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean copy(Path sourceFile, Path targetFile) {
        this.createFileIfNotExists(targetFile);
        try {
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean copyDefaultFile(String defaultFileName, Path targetFile) {
        if (targetFile.toFile().exists() && !HartshornUtils.isFileEmpty(targetFile)) return false;
        return Hartshorn.getResourceFile(defaultFileName)
                .map(resource -> this.copy(resource, targetFile))
                .or(false);
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) throws ApplicationException {
        for (InjectorProperty<?> property : properties) {
            if (property instanceof FileTypeProperty) {
                final FileType fileType = ((FileTypeProperty) property).getObject();

                if (fileType.getType().equals(PersistenceType.RAW)) {
                    this.setFileType(fileType);
                }
                else {
                    throw new IllegalArgumentException("Unsupported persistence type: " + fileType.getType() + ", expected: " + PersistenceType.RAW);
                }
                break;
            }
            else if (property instanceof PersistenceProperty) {
                this.mapper.stateEnabling(property);
            }
        }
    }

    @Override
    public <T> Exceptional<T> read(Path file, Class<T> type) {
        return this.mapper.read(file, type);
    }

    @Override
    public <T> Exceptional<T> read(Path file, GenericType<T> type) {
        return this.mapper.read(file, type);
    }

    @Override
    public <T> Exceptional<Boolean> write(Path file, T content) {
        return this.mapper.write(file, content);
    }
}
