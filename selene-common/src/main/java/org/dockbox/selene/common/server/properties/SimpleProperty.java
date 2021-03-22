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

package org.dockbox.selene.common.server.properties;

import org.dockbox.selene.api.server.properties.InjectorProperty;

public class SimpleProperty<T> implements InjectorProperty<T> {

    private final T object;
    private final String key;

    public SimpleProperty(T object, String key) {
        this.object = object;
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public T getObject() {
        return this.object;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        return (Class<T>) this.getObject().getClass();
    }
}
