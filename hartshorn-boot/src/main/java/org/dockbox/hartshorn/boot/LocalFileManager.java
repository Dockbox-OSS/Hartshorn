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

package org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.persistence.DefaultAbstractFileManager;
import org.dockbox.hartshorn.persistence.FileManager;

import java.nio.file.Path;
import java.nio.file.Paths;

@Binds(FileManager.class)
public class LocalFileManager extends DefaultAbstractFileManager {

    @Override
    public Path data() {
        return this.root().resolve("data");
    }

    @Override
    public Path logs() {
        return this.root().resolve("logs");
    }

    @Override
    public Path root() {
        return Paths.get("");
    }

    @Override
    public Path configs() {
        return this.root().resolve("config");
    }
}
