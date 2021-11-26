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

package org.dockbox.hartshorn.persistence.remote;

import java.nio.file.Path;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DerbyFileRemote implements Remote<Path> {

    public static final DerbyFileRemote INSTANCE = new DerbyFileRemote();

    @Override
    public PersistenceConnection connection(Path target, String user, String password) {
        return new PersistenceConnection(
                "jdbc:derby:directory:%s/db;create=true".formatted(target.toFile().getAbsolutePath()),
                user,
                password,
                this
        );
    }

    @Override
    public String driver() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }
}
