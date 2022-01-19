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
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.stereotype.Component;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.annotations.activate.ServiceActivator;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

@Getter
public class ComponentContainerImpl implements ComponentContainer {

    private final Component annotation;
    private final TypeContext<?> component;
    private final List<Class<? extends Annotation>> activators = new CopyOnWriteArrayList<>();
    private final ApplicationContext context;

    public ComponentContainerImpl(final ApplicationContext context, final TypeContext<?> component) {
        final Exceptional<Component> annotated = component.annotation(Component.class);
        if (annotated.absent()) throw new IllegalArgumentException("Provided component candidate has no assigned decorator");

        this.component = component;
        this.annotation = annotated.get();
        this.context = context;

        final Exceptional<Service> service = component.annotation(Service.class);
        if (service.present()) {
            this.activators.addAll(HartshornUtils.asList(service.get().activators()));
        }
    }

    @Override
    public String id() {
        final String id = this.annotation.id();
        if ("".equals(id)) return ComponentContainer.id(this.context, this.component, true);
        return id;
    }

    @Override
    public String name() {
        final String name = this.annotation.name();
        if ("".equals(name)) return ComponentContainer.name(this.context, this.component, true);
        return name;
    }

    @Override
    public boolean enabled() {
        return this.annotation.enabled();
    }

    @Override
    public TypeContext<?> type() {
        return this.component;
    }

    @Override
    public TypeContext<?> owner() {
        return TypeContext.of(this.annotation.owner());
    }

    @Override
    public List<Class<? extends Annotation>> activators() {
        return Collections.unmodifiableList(this.activators);
    }

    @Override
    public boolean hasActivator() {
        return !(this.activators.isEmpty() || (this.activators.size() == 1 && Service.class.equals(this.activators.get(0))));
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        if (!TypeContext.of(activator).annotation(ServiceActivator.class).present())
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        return this.activators().contains(activator);
    }

    @Override
    public boolean singleton() {
        return this.annotation.singleton();
    }

    @Override
    public ComponentType componentType() {
        return this.annotation.type();
    }

    @Override
    public boolean permitsProxying() {
        return this.annotation.permitProxying();
    }

    @Override
    public boolean permitsProcessing() {
        return this.annotation.permitProcessing();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final ComponentContainerImpl that = (ComponentContainerImpl) o;
        return this.component.equals(that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.component);
    }
}
