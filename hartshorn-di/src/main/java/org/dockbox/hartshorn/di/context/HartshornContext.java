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

package org.dockbox.hartshorn.di.context;

import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.properties.Attribute;

import javax.inject.Named;

public interface HartshornContext extends Context {

    default <T> T get(final TypeContext<T> type, final Named named) {
        return this.get(type.type(), named);
    }

    <T> T get(Class<T> type, Named named);

    <T> T get(Key<T> key, final Attribute<?>... additionalProperties);

    default <T> T get(final TypeContext<T> type, final Attribute<?>... additionalProperties) {
        return this.get(type.type(), additionalProperties);
    }

    <T> T get(Class<T> type, Attribute<?>... additionalProperties);

    default <T> T get(final TypeContext<T> type, final Object... varargs) {
        return this.get(type.type(), varargs);
    }

    <T> T get(Class<T> type, Object... varargs);

}
