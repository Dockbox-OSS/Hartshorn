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

import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.HashSetMultiMap;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.annotations.stereotype.Component;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Collection;
import java.util.List;

import lombok.Getter;

public class ComponentLocatorImpl implements ComponentLocator {

    private final MultiMap<String, ComponentContainer> cache = new HashSetMultiMap<>();
    @Getter
    private final ApplicationContext applicationContext;

    public ComponentLocatorImpl(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void register(final String prefix) {
        if (this.cache.containsKey(prefix)) return;

        this.applicationContext().log().debug("Registering prefix '" + prefix + "' for component locating");

        final long start = System.currentTimeMillis();

        final List<ComponentContainer> containers = this.applicationContext().environment()
                .types(prefix, Component.class, false)
                .stream()
                .map(type -> new ComponentContainerImpl(this.applicationContext(), type))
                .filter(ComponentContainerImpl::enabled)
                .filter(container -> !container.type().isAnnotation()) // Exclude extended annotations
                .map(ComponentContainer.class::cast)
                .filter(container -> container.activators().stream().allMatch(this.applicationContext()::hasActivator))
                .toList();

        final long duration = System.currentTimeMillis() - start;
        this.applicationContext().log().info("Located %d components with prefix %s in %dms".formatted(containers.size(), prefix, duration));

        this.cache.putAll(prefix, containers);
    }

    @Override
    public Collection<ComponentContainer> containers() {
        return this.cache.entrySet().stream().flatMap(a -> a.getValue().stream()).toList();
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

    @Override
    public <T> void validate(final Key<T> key) {
        final TypeContext<T> contract = key.type();
        if (contract.annotation(Component.class).present() && this.container(contract).absent()) {
            this.applicationContext().log().warn("Component key '%s' is annotated with @Component, but is not registered.".formatted(contract.qualifiedName()));
        }
    }
}
