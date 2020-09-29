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

import org.dockbox.selene.core.util.files.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class TestFileUtils implements FileUtils {
    @NotNull
    @Override
    public Path getDataDir() {
        return null;
    }

    @NotNull
    @Override
    public Path getLogsDir() {
        return null;
    }

    @NotNull
    @Override
    public Path getServerRoot() {
        return null;
    }

    @NotNull
    @Override
    public Path getExtensionDir() {
        return null;
    }

    @NotNull
    @Override
    public Path getModDir() {
        return null;
    }

    @NotNull
    @Override
    public Path getPluginDir() {
        return null;
    }

    @NotNull
    @Override
    public Path getExtensionConfigdir() {
        return null;
    }

    @NotNull
    @Override
    public Path getModConfigDir() {
        return null;
    }

    @NotNull
    @Override
    public Path getPluginConfigDir() {
        return null;
    }

    @NotNull
    @Override
    public Path createPathIfNotExists(@NotNull Path path) {
        return null;
    }

    @NotNull
    @Override
    public Path createFileIfNotExists(@NotNull Path file) {
        return null;
    }
}
