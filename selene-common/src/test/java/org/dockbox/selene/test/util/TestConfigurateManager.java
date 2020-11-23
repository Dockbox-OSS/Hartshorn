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

package org.dockbox.selene.test.util;

import org.dockbox.selene.core.impl.util.files.DefaultConfigurateManager;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.util.files.FileType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestConfigurateManager extends DefaultConfigurateManager {

    private final Path tempDirRoot;

    protected TestConfigurateManager() throws IOException {
        super(FileType.JSON);
        this.tempDirRoot = Files.createTempDirectory("SeleneTesting");
    }

    @NotNull
    @Override
    public Path getDataDir() {
        return this.tempDirRoot.resolve("data");
    }

    @NotNull
    @Override
    public Path getLogsDir() {
        return this.tempDirRoot.resolve("logs");
    }

    @NotNull
    @Override
    public Path getServerRoot() {
        return this.tempDirRoot;
    }

    @NotNull
    @Override
    public Path getExtensionDir() {
        return this.tempDirRoot.resolve("extensions");
    }

    @NotNull
    @Override
    public Exceptional<Path> getModDir() {
        return Exceptional.of(this.tempDirRoot.resolve("mods"));
    }

    @NotNull
    @Override
    public Path getPluginDir() {
        return this.tempDirRoot.resolve("plugins");
    }

    @NotNull
    @Override
    public Path getExtensionConfigsDir() {
        return this.tempDirRoot.resolve("config/extensions");
    }

    @NotNull
    @Override
    public Exceptional<Path> getModdedPlatformModsConfigDir() {
        return Exceptional.of(this.tempDirRoot.resolve("config/mods"));
    }

    @NotNull
    @Override
    public Path getPlatformPluginsConfigDir() {
        return this.tempDirRoot.resolve("config/plugins");
    }
}
