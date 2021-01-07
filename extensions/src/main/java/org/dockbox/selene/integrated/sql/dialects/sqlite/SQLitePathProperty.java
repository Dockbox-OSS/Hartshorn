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

package org.dockbox.selene.integrated.sql.dialects.sqlite;

import org.dockbox.selene.core.server.properties.InjectorProperty;

import java.nio.file.Path;

public class SQLitePathProperty implements InjectorProperty<Path> {

    private final Path path;

    public SQLitePathProperty(Path path) {
        this.path = path;
    }

    @Override
    public String getKey() {
        return SQLiteMan.PATH_KEY;
    }

    @Override
    public Path getObject() {
        return this.path;
    }
}
