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

package org.dockbox.selene.sponge.util.files;

import org.dockbox.selene.core.files.FileManager;
import org.dockbox.selene.core.files.FileType;
import org.dockbox.selene.core.impl.files.DefaultConfigurateManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Uses SpongeDefaultFileManager to determine the directory paths. This way all directory paths can be reused by other
 * FileManager implementations without the need of redefining these when changes are made. Due to multiple inheritance
 * not being possible in Java, a interface is used for this purpose, requiring us to directly target the interface super.
 */
public class SpongeConfigurateManager extends DefaultConfigurateManager implements SpongeDefaultFileManager {

    /**
     * Provides the given {@link FileType} to the super type {@link FileManager}.
     */
    protected SpongeConfigurateManager() {
        super(FileType.YAML);
    }

    @NotNull
    @Override
    public Path getDataDir() {
        return SpongeDefaultFileManager.super.getDataDir();
    }

    @NotNull
    @Override
    public Path getLogsDir() {
        return SpongeDefaultFileManager.super.getLogsDir();
    }

    @NotNull
    @Override
    public Path getServerRoot() {
        return SpongeDefaultFileManager.super.getServerRoot();
    }

    @NotNull
    @Override
    public Path getExtensionDir() {
        return SpongeDefaultFileManager.super.getExtensionDir();
    }

    @NotNull
    @Override
    public Exceptional<Path> getModDir() {
        return SpongeDefaultFileManager.super.getModDir();
    }

    @NotNull
    @Override
    public Path getPluginDir() {
        return SpongeDefaultFileManager.super.getPluginDir();
    }

    @NotNull
    @Override
    public Path getExtensionConfigsDir() {
        return SpongeDefaultFileManager.super.getExtensionConfigsDir();
    }

    @NotNull
    @Override
    public Exceptional<Path> getModdedPlatformModsConfigDir() {
        return SpongeDefaultFileManager.super.getModdedPlatformModsConfigDir();
    }

    @NotNull
    @Override
    public Path getPlatformPluginsConfigDir() {
        return SpongeDefaultFileManager.super.getPlatformPluginsConfigDir();
    }
}
