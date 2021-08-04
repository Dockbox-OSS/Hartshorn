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

package org.dockbox.hartshorn.persistence.registry;

import org.jetbrains.annotations.NonNls;

public interface RegistryIdentifier {
    @NonNls
    default String key() {
        if (this.getClass().isEnum()) {
            return ((Enum<?>) this).name();
        }
        throw new UnsupportedOperationException("Non-enum type " + this.getClass().getSimpleName() + " does not override key()");
    }

    default boolean same(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistryIdentifier that)) return false;
        return this.key().equals(that.key());
    }
}
