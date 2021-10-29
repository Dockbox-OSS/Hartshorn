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

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.ArrayListMultiMap;
import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.annotations.component.Component;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Collection;
import java.util.List;

public class ComponentLocatorImpl implements ComponentLocator {

    private static final MultiMap<String, ComponentContainer> cache = new ArrayListMultiMap<>();
    private final ApplicationContext context;

    public ComponentLocatorImpl(final ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void register(final String prefix) {
        if (ComponentLocatorImpl.cache.containsKey(prefix)) return;

        this.context.log().debug("Registering prefix '" + prefix + "' for component locating");

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

        ComponentLocatorImpl.cache.putAll(prefix, containers);
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
