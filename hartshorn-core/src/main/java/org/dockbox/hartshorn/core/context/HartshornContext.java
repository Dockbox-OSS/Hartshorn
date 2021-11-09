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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import javax.inject.Named;

public interface HartshornContext extends Context {

    default <T> T get(final TypeContext<T> type, final Named named) {
        return this.get(Key.of(type, named));
    }

    default <T> T get(Class<T> type, Named named) {
        return this.get(Key.of(type, named));
    }

    <T> T get(Key<T> key);

    default <T> T get(final TypeContext<T> type) {
        return this.get(Key.of(type));
    }

    default <T> T get(Class<T> type) {
        return this.get(Key.of(type));
    };

}
