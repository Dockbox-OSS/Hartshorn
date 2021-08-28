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

package org.dockbox.hartshorn.persistence.properties;

import org.dockbox.hartshorn.di.properties.Attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ConnectionAttribute implements Attribute<PersistenceConnection> {
    @Getter private final PersistenceConnection value;

    public static ConnectionAttribute of(Object target) {
        return of(target, "", "");
    }

    public static ConnectionAttribute of(Object target, String user, String password) {
        return of(Remote.DERBY, target, user, password);
    }

    public static ConnectionAttribute of(Remote remote, Object target, String user, String password) {
        return of(remote.connection(target, user, password));
    }

    public static ConnectionAttribute of(PersistenceConnection connection) {
        return new ConnectionAttribute(connection);
    }
}
