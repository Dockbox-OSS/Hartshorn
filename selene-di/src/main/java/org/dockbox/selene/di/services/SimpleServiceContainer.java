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

import org.dockbox.selene.api.config.Environment;
import org.dockbox.selene.di.annotations.Service;
import org.dockbox.selene.util.SeleneUtils;

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
        if (!service.isAnnotationPresent(Service.class)) throw new IllegalArgumentException("Provided type is not a service");
        this.annotation = service.getAnnotation(Service.class);
        this.id = SeleneUtils.serviceId(service);
        this.name = SeleneUtils.serviceName(service);
        this.dependencies = SeleneUtils.asUnmodifiableList(this.annotation.dependencies());
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
    public Environment getEnvironment() {
        return this.annotation.environment();
    }
}
