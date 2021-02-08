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

package org.dockbox.selene.common.files;

import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.files.FileType;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class DefaultAbstractFileManager extends FileManager
{

    protected DefaultAbstractFileManager(FileType fileType)
    {
        super(fileType);
    }

    @NotNull
    @Override
    public Path getDataFile(@NotNull Module module)
    {
        return this.getDataFile(module, module.id());
    }

    @NotNull
    @Override
    public Path getConfigFile(@NotNull Module module)
    {
        return this.getConfigFile(module, module.id());
    }

    @NotNull
    @Override
    public Path getDataFile(@NotNull Module module, @NotNull String file)
    {
        return this.createFileIfNotExists(
                this.getFileType().asPath(
                        this.getDataDir().resolve(module.id()),
                        file
                )
        );
    }

    @NotNull
    @Override
    public Path getConfigFile(@NotNull Module module, @NotNull String file)
    {
        return this.createFileIfNotExists(
                this.getFileType().asPath(
                        this.getModuleConfigsDir().resolve(module.id()),
                        file
                )
        );
    }

    @NotNull
    @Override
    public Path createPathIfNotExists(@NotNull Path path)
    {
        return SeleneUtils.createPathIfNotExists(path);
    }

    @NotNull
    @Override
    public Path createFileIfNotExists(@NotNull Path file)
    {
        return SeleneUtils.createFileIfNotExists(file);
    }

    @Override
    public boolean move(Path sourceFile, Path targetFile)
    {
        this.createFileIfNotExists(targetFile);
        try
        {
            Files.move(sourceFile, targetFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    @Override
    public boolean copy(Path sourceFile, Path targetFile)
    {
        this.createFileIfNotExists(targetFile);
        try
        {
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    @Override
    public boolean copyDefaultFile(String defaultFileName, Path targetFile)
    {
        if (targetFile.toFile().exists() && !SeleneUtils.isFileEmpty(targetFile)) return false;
        return Selene.getResourceFile(defaultFileName)
                .map(resource -> this.copy(resource, targetFile))
                .orElse(false);
    }
}
