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

package org.dockbox.selene.structures;

import com.google.common.io.Files;

import org.dockbox.selene.core.impl.files.DefaultXStreamManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.util.SeleneUtils;

import java.nio.file.Path;
import java.util.Map;

public class TestXStreamFileManager extends DefaultXStreamManager {

    private final Map<String, Path> pathDictionary = SeleneUtils.emptyMap();

    @Override
    public Path getDataDir() {
        return this.getPath("data");
    }

    @Override
    public Path getLogsDir() {
        return this.getPath("logs");
    }

    @Override
    public Path getServerRoot() {
        return this.getPath("root");
    }

    @Override
    public Path getExtensionDir() {
        return this.getPath("extensions");
    }

    @Override
    public Exceptional<Path> getModDir() {
        return Exceptional.of(this.getPath("mods"));
    }

    @Override
    public Path getPluginDir() {
        return this.getPath("plugins");
    }

    @Override
    public Path getExtensionConfigsDir() {
        return this.getPath("config/extension");
    }

    @Override
    public Exceptional<Path> getModdedPlatformModsConfigDir() {
        return Exceptional.of(this.getPath("config/mods"));
    }

    @Override
    public Path getPlatformPluginsConfigDir() {
        return this.getPath("config/plugins");
    }

    private Path getPath(String id) {
        if (!this.pathDictionary.containsKey(id)) {
            this.pathDictionary.put(id, Files.createTempDir().toPath());
        }
        return this.pathDictionary.get(id);
    }
}
