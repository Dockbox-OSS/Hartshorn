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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.TypedOwner;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.GenericType;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.persistence.mapping.ObjectMapper;
import org.dockbox.hartshorn.persistence.properties.ModifiersAttribute;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DefaultAbstractFileManager implements FileManager {

    @Inject
    private ObjectMapper mapper;

    @Inject
    @Getter
    private ApplicationContext context;

    public Path dataFile(final Class<?> owner) {
        return this.dataFile(this.owner(owner));
    }

    @NotNull
    @Override
    public Path dataFile(@NotNull final TypedOwner owner) {
        return this.dataFile(owner, owner.id());
    }

    public FileType fileType() {
        return this.mapper.fileType();
    }

    @NotNull
    @Override
    public Path configFile(@NotNull final TypedOwner owner) {
        return this.configFile(owner, owner.id());
    }

    @NotNull
    @Override
    public Path dataFile(@NotNull final TypedOwner owner, @NotNull final String file) {
        this.context().log().debug("Requesting data file '" + file + "' for " + owner.id());
        return this.createFileIfNotExists(this.fileType().asPath(this.data().resolve(owner.id()), file));
    }

    @NotNull
    @Override
    public Path configFile(@NotNull final TypedOwner owner, @NotNull final String file) {
        this.context().log().debug("Requesting config file '" + file + "' for " + owner.id());
        return this.createFileIfNotExists(this.fileType().asPath(this.configs().resolve(owner.id()), file));
    }

    @Override
    public <T> Exceptional<T> read(final Path file, final Class<T> type) {
        return this.mapper.read(file, type);
    }

    @Override
    public <T> Exceptional<T> read(final Path file, final GenericType<T> type) {
        return this.mapper.read(file, type);
    }

    @Override
    public <T> Exceptional<Boolean> write(final Path file, final T content) {
        return this.mapper.write(file, content);
    }

    @Override
    public Exceptional<Boolean> write(final Path file, final String content) {
        return Exceptional.of(() -> {
            this.context().log().debug("Writing raw string content to " + file);
            final BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()));
            writer.write(content);
            writer.close();
            return true;
        }).orElse(() -> false);
    }

    @NotNull
    @Override
    public Path createPathIfNotExists(@NotNull final Path path) {
        return HartshornUtils.createPathIfNotExists(path);
    }

    @NotNull
    @Override
    public Path createFileIfNotExists(@NotNull final Path file) {
        return HartshornUtils.createFileIfNotExists(file);
    }

    @Override
    public boolean move(final Path sourceFile, final Path targetFile) {
        this.context().log().debug("Moving " + sourceFile + " to " + targetFile);
        this.createFileIfNotExists(targetFile);
        try {
            Files.move(sourceFile, targetFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (final IOException e) {
            return false;
        }
    }

    @Override
    public boolean copy(final Path sourceFile, final Path targetFile) {
        this.context().log().debug("Copying " + sourceFile + " to " + targetFile);
        this.createFileIfNotExists(targetFile);
        try {
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (final IOException e) {
            return false;
        }
    }

    @Override
    public boolean copyDefaultFile(final String defaultFileName, final Path targetFile) {
        if (targetFile.toFile().exists() && !HartshornUtils.empty(targetFile)) return false;
        return Hartshorn.resource(defaultFileName)
                .map(resource -> this.copy(resource, targetFile))
                .or(false);
    }

    @Override
    public void apply(final Attribute<?> property) throws ApplicationException {
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

    protected void fileType(final FileType fileType) {
        this.mapper.fileType(fileType);
    }
}
