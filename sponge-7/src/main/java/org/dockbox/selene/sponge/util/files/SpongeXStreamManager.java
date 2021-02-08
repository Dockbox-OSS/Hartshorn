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

import org.dockbox.selene.core.impl.files.DefaultXStreamManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class SpongeXStreamManager extends DefaultXStreamManager implements SpongeDefaultFileManager
{

    @NotNull
    @Override
    public Path getDataDir()
    {
        return SpongeDefaultFileManager.super.getDataDir();
    }

    @NotNull
    @Override
    public Path getLogsDir()
    {
        return SpongeDefaultFileManager.super.getLogsDir();
    }

    @NotNull
    @Override
    public Path getServerRoot()
    {
        return SpongeDefaultFileManager.super.getServerRoot();
    }

    @NotNull
    @Override
    public Path getModuleDir()
    {
        return SpongeDefaultFileManager.super.getModuleDir();
    }

    @NotNull
    @Override
    public Exceptional<Path> getModDir()
    {
        return SpongeDefaultFileManager.super.getModDir();
    }

    @NotNull
    @Override
    public Path getPluginDir()
    {
        return SpongeDefaultFileManager.super.getPluginDir();
    }

    @NotNull
    @Override
    public Path getModuleConfigsDir()
    {
        return SpongeDefaultFileManager.super.getModuleConfigsDir();
    }

    @NotNull
    @Override
    public Exceptional<Path> getModdedPlatformModsConfigDir()
    {
        return SpongeDefaultFileManager.super.getModdedPlatformModsConfigDir();
    }

    @NotNull
    @Override
    public Path getPlatformPluginsConfigDir()
    {
        return SpongeDefaultFileManager.super.getPlatformPluginsConfigDir();
    }
}
