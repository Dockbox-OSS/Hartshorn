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

package org.dockbox.hartshorn.test.files;

import org.dockbox.hartshorn.persistence.DefaultAbstractFileManager;
import org.dockbox.hartshorn.test.JUnit5Application;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class JUnitFileManager extends DefaultAbstractFileManager {

    @NotNull
    public Path data() {
        return this.root().resolve("data/");
    }

    @NotNull
    public Path logs() {
        return this.root().resolve("logs/");
    }

    @NotNull
    public Path root() {
        return JUnit5Application.information().path();
    }

    @NotNull
    public Path configs() {
        return this.root().resolve("config/");
    }

}
