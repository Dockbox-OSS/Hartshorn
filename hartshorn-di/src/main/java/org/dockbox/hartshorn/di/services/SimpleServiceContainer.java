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

import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.lang.annotation.Annotation;
import java.util.List;

import lombok.Getter;

@Getter
public class SimpleServiceContainer implements ServiceContainer {

    private final String id;
    private final String name;
    private final List<String> dependencies;
    private final Service annotation;
    private final Class<?> type;

    public SimpleServiceContainer(Class<?> service) {
        if (!ApplicationContextAware.instance().getContext().meta().isComponent(service)) throw new IllegalArgumentException("Provided type is not a component");
        this.annotation = service.getAnnotation(Service.class);
        this.id = Bindings.serviceId(service);
        this.name = Bindings.serviceName(service);
        this.dependencies = HartshornUtils.asUnmodifiableList(this.annotation.dependencies());
        this.type = service;
    }

    @Override
    public boolean isEnabled() {
        return !this.annotation.disabled();
    }

    @Override
    public Class<?> getOwner() {
        return this.annotation.owner();
    }

    @Override
    public Class<? extends Annotation> getActivator() {
        return this.annotation.activator();
    }

    @Override
    public boolean hasActivator() {
        return !Service.class.equals(this.getActivator());
    }

    @Override
    public boolean isSingleton() {
        return this.annotation.singleton();
    }
}
