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
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ComponentLocatorImpl implements ComponentLocator {

    private static final Map<String, Collection<ComponentContainer>> cache = HartshornUtils.emptyConcurrentMap();
    private final ApplicationContext context;

    public ComponentLocatorImpl(final ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void register(final String prefix) {
        if (ComponentLocatorImpl.cache.containsKey(prefix)) return;

        final long start = System.currentTimeMillis();
        this.context.environment().prefix(prefix);

        final Collection<TypeContext<?>> types = this.context.environment().types(Component.class);

        final List<ComponentContainer> containers = types.stream()
                .map(type -> new ComponentContainerImpl(this.context, type))
                .filter(ComponentContainerImpl::enabled)
                .filter(container -> !container.type().isAnnotation()) // Exclude extended annotations
                .map(ComponentContainer.class::cast)
                .toList();

        final long duration = System.currentTimeMillis() - start;
        this.context.log().info("Collected %d types and %d components in %dms".formatted(types.size(), containers.size(), duration));

        ComponentLocatorImpl.cache.put(prefix, containers);
    }

    @Override
    public Collection<ComponentContainer> containers() {
        return cache.entrySet().stream().flatMap(a -> a.getValue().stream()).toList();
    }

    @Override
    public Collection<ComponentContainer> containers(final ComponentType componentType) {
        return this.containers().stream()
                .filter(container -> container.componentType() == componentType)
                .toList();
    }

    @Override
    public Exceptional<ComponentContainer> container(final TypeContext<?> type) {
        return Exceptional.of(this.containers()
                .stream()
                .filter(container -> container.type().equals(type))
                .findFirst()
        );
    }
}
