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

package org.dockbox.hartshorn.di.services;

import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.util.Reflect;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleServiceLocator implements ServiceLocator {

    private static final Map<String, Collection<ServiceContainer>> cache = HartshornUtils.emptyConcurrentMap();

    @Override
    public @NotNull @Unmodifiable Collection<Class<?>> locate(String prefix) {
        if (SimpleServiceLocator.cache.containsKey(prefix)) {
            return SimpleServiceLocator.cache.get(prefix).stream()
                    .map(ServiceContainer::getType)
                    .collect(Collectors.toList());
        }
        final Collection<Class<?>> types = Reflect.annotatedTypes(prefix, Service.class);

        final List<ServiceContainer> containers = types.stream()
                .map(SimpleServiceContainer::new)
                .map(ServiceContainer.class::cast)
                .toList();
        SimpleServiceLocator.cache.put(prefix, containers);

        return HartshornUtils.asUnmodifiableCollection(types);
    }

    @Override
    public Collection<ServiceContainer> containers() {
        return cache.entrySet().stream().flatMap(a -> a.getValue().stream()).toList();
    }
}
