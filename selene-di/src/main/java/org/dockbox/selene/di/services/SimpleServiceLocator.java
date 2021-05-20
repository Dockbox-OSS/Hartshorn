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

package org.dockbox.selene.di.services;

import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.util.Reflect;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Map;

public class SimpleServiceLocator implements ServiceLocator {

    private static final Map<String, Collection<Class<?>>> cache = SeleneUtils.emptyConcurrentMap();

    @Override
    public @NotNull @Unmodifiable Collection<Class<?>> locate(String prefix) {
        if (SimpleServiceLocator.cache.containsKey(prefix)) {
            return SimpleServiceLocator.cache.get(prefix);
        }
        final Collection<Class<?>> types = Reflect.annotatedTypes(prefix, Service.class);
        SimpleServiceLocator.cache.put(prefix, types);
        return SeleneUtils.asUnmodifiableCollection(types);
    }
}
