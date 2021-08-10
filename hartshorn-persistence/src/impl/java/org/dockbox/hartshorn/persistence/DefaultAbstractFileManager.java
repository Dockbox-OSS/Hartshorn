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
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.persistence.mapping.GenericType;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.persistence.properties.ModifiersAttribute;
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

    public Path dataFile(Class<?> owner) {
        return this.dataFile(this.owner(owner));
    }

    @NotNull
    @Override
    public Path dataFile(@NotNull TypedOwner owner) {
        return this.dataFile(owner, owner.id());
    }

    public FileType fileType() {
        return this.mapper.fileType();
    }

    @NotNull
    @Override
    public Path configFile(@NotNull TypedOwner owner) {
        return this.configFile(owner, owner.id());
    }

    @NotNull
    @Override
    public Path dataFile(@NotNull TypedOwner owner, @NotNull String file) {
        return this.createFileIfNotExists(this.fileType().asPath(this.data().resolve(owner.id()), file));
    }

    @NotNull
    @Override
    public Path configFile(@NotNull TypedOwner owner, @NotNull String file) {
        return this.createFileIfNotExists(this.fileType().asPath(this.serviceConfigs().resolve(owner.id()), file));
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
        if (targetFile.toFile().exists() && !HartshornUtils.empty(targetFile)) return false;
        return Hartshorn.resource(defaultFileName)
                .map(resource -> this.copy(resource, targetFile))
                .or(false);
    }

    @Override
    public void apply(Attribute<?> property) throws ApplicationException {
        if (property instanceof FileTypeAttribute) {
            final FileType fileType = ((FileTypeAttribute) property).value();

            if (fileType.type().equals(PersistenceType.RAW)) {
                this.fileType(fileType);
            }
            else {
                throw new IllegalArgumentException("Unsupported persistence type: " + fileType.type() + ", expected: " + PersistenceType.RAW);
            }
        }
        else if (property instanceof ModifiersAttribute) {
            this.mapper.apply(property);
        }
    }

    protected void fileType(FileType fileType) {
        this.mapper.fileType(fileType);
    }
}
