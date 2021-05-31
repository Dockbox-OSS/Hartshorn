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
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class DefaultAbstractFileManager implements FileManager {

    @Wired
    private ApplicationContext context;
    private FileType fileType;

    protected DefaultAbstractFileManager(FileType fileType) {
        this.fileType = fileType;
    }

    public FileType getFileType() {
        return this.fileType;
    }

    protected void setFileType(FileType fileType) {
        this.fileType = fileType;
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
        return SeleneUtils.createPathIfNotExists(path);
    }

    @NotNull
    @Override
    public Path createFileIfNotExists(@NotNull Path file) {
        return SeleneUtils.createFileIfNotExists(file);
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
        if (targetFile.toFile().exists() && !SeleneUtils.isFileEmpty(targetFile)) return false;
        return Selene.getResourceFile(defaultFileName)
                .map(resource -> this.copy(resource, targetFile))
                .or(false);
    }

    @SuppressWarnings("unchecked")
    protected <T> Exceptional<T> correctPersistentCapable(Path file, Class<T> type) {
        if (Reflect.assignableFrom(PersistentCapable.class, type)) {
            // Provision basis is required here, as injected types will typically pass in a interface type. If no injection point is available a
            // regular instance is created through available constructors.
            Class<? extends PersistentModel<?>> modelType = ((PersistentCapable<?>) this.context.get(type)).getModelClass();
            @NotNull Exceptional<? extends PersistentModel<?>> model = this.read(file, modelType);
            return model.map(PersistentModel::toPersistentCapable).map(content -> (T) content);
        }
        return Exceptional.none();
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        for (InjectorProperty<?> property : properties)
            if (property instanceof FileTypeProperty) {
                final FileType fileType = ((FileTypeProperty) property).getObject();

                if (fileType.getType().equals(PersistenceType.RAW)) {
                    this.setFileType(fileType);
                } else {
                    throw new IllegalArgumentException("Unsupported persistence type: " + fileType.getType() + ", expected: " + PersistenceType.RAW);
                }
                break;
            }
    }
}
