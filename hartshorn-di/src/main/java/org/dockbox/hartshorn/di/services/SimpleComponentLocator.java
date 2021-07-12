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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.ComponentType;
import org.dockbox.hartshorn.di.annotations.component.Component;
import org.dockbox.hartshorn.di.annotations.component.ComponentLike;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleComponentLocator implements ComponentLocator {

    private static final Map<String, Collection<ComponentContainer>> cache = HartshornUtils.emptyConcurrentMap();
    private static final List<Class<? extends Annotation>> decorators = HartshornUtils.emptyList();

    @Override
    public @NotNull @Unmodifiable Collection<Class<?>> locate(String prefix) {
        if (SimpleComponentLocator.cache.containsKey(prefix)) {
            return SimpleComponentLocator.cache.get(prefix).stream()
                    .map(ComponentContainer::type)
                    .collect(Collectors.toList());
        }

        Reflect.prefix(prefix);
        final Collection<Class<?>> types = Reflect.types(Component.class);

        final List<ComponentContainer> containers = types.stream()
                .map(SimpleComponentContainer::new)
                .filter(SimpleComponentContainer::enabled)
                .map(ComponentContainer.class::cast)
                .toList();
        SimpleComponentLocator.cache.put(prefix, containers);

        return HartshornUtils.asUnmodifiableSet(types);
    }

    @Override
    public @NotNull @Unmodifiable Collection<Class<?>> locate(String prefix, ComponentType componentType) {
        return HartshornUtils.asUnmodifiableSet(this.locate(prefix).stream()
                .map(this::container)
                .map(Exceptional::get)
                .filter(container -> container.componentType().equals(componentType))
                .map(ComponentContainer::type)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public Collection<ComponentContainer> containers() {
        return cache.entrySet().stream().flatMap(a -> a.getValue().stream()).toList();
    }

    @Override
    public Exceptional<ComponentContainer> container(Class<?> type) {
        return Exceptional.of(this.containers()
                .stream()
                .filter(container -> container.type().equals(type))
                .findFirst()
        );
    }

    @Override
    public Collection<Class<? extends Annotation>> decorators() {
        if (decorators.isEmpty()) {
            final Collection<Class<?>> annotations = Reflect.types(ComponentLike.class);
            for (Class<?> annotation : annotations) {
                if (annotation.isAnnotation()) //noinspection unchecked
                    decorators.add((Class<? extends Annotation>) annotation);
            }
        }
        return decorators;
    }
}
