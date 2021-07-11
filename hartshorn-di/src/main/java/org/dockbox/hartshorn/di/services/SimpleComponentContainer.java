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
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.annotation.Annotation;
import java.util.List;

import lombok.Getter;

@Getter
public class SimpleComponentContainer<A extends Annotation> implements ComponentContainer {

    private final Component annotation;
    private final Class<?> component;
    private final List<Class<? extends Annotation>> activators = HartshornUtils.emptyList();

    public SimpleComponentContainer(Class<?> component) {
        final Exceptional<Component> annotated = Reflect.annotation(component, Component.class);
        if (annotated.absent()) throw new IllegalArgumentException("Provided component candidate has no assigned decorator");

        this.component = component;
        this.annotation = annotated.get();

        final Exceptional<Service> service = Reflect.annotation(component, Service.class);
        if (service.present()) {
            this.activators.addAll(HartshornUtils.asList(service.get().activators()));
        }
    }

    @Override
    public String getId() {
        final String id = this.annotation.id();
        if ("".equals(id)) return Bindings.serviceId(this.component, true);
        return id;
    }

    @Override
    public String getName() {
        final String name = this.annotation.name();
        if ("".equals(name)) return Bindings.serviceName(this.component, true);
        return name;
    }

    @Override
    public boolean enabled() {
        return this.annotation.enabled();
    }

    @Override
    public Class<?> getType() {
        return this.component;
    }

    @Override
    public Class<?> owner() {
        return this.annotation.owner();
    }

    @Override
    public List<Class<? extends Annotation>> activators() {
        return HartshornUtils.asUnmodifiableList(this.activators);
    }

    @Override
    public boolean hasActivator() {
        return !(this.activators.isEmpty() || (this.activators.size() == 1 && Service.class.equals(this.activators.get(0))));
    }

    @Override
    public boolean hasActivator(Class<? extends Annotation> activator) {
        if (!Reflect.annotation(activator, ServiceActivator.class).present())
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
}
