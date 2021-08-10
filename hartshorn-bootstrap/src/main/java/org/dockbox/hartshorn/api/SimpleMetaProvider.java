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

package org.dockbox.hartshorn.api;

import org.dockbox.hartshorn.api.domain.SimpleTypedOwner;
import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.di.InjectorMetaProvider;

/**
 * An extension of {@link InjectorMetaProvider} which adds {@link Hartshorn}
 * as a valid {@link TypedOwner}. It is up to the final implementation to
 * decide whether this should be used.
 */
public class SimpleMetaProvider extends InjectorMetaProvider {

    @Override
    public TypedOwner lookup(final Class<?> type) {
        if (Hartshorn.class.equals(type)) {
            return SimpleTypedOwner.of(Hartshorn.PROJECT_ID);
        }
        return super.lookup(type);
    }
}
