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

package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.MetaProvider;
import org.dockbox.hartshorn.api.domain.SimpleTypedOwner;
import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.api.entity.annotations.Entity;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.services.ServiceContainer;
import org.dockbox.hartshorn.util.Reflect;

import javax.inject.Singleton;

public class InjectorMetaProvider implements MetaProvider {

    @Override
    public TypedOwner lookup(Class<?> type) {
        if (type.isAnnotationPresent(Entity.class)) {
            return SimpleTypedOwner.of(type.getAnnotation(Entity.class).value());
        }
        else {
            final Exceptional<ServiceContainer> container = ApplicationContextAware.instance().getContext().locator().container(type);
            if (container.present()) {
                final ServiceContainer service = container.get();
                if (Reflect.notVoid(service.getOwner())) return this.lookup(service.getOwner());
            }
        }
        return SimpleTypedOwner.of(Bindings.serviceId(type));
    }

    @Override
    public boolean isSingleton(Class<?> type) {
        if (type.isAnnotationPresent(Singleton.class)) return true;
        if (type.isAnnotationPresent(com.google.inject.Singleton.class)) return true;

        return ApplicationContextAware.instance()
                .getContext()
                .locator()
                .container(type)
                .map(ServiceContainer::isSingleton)
                .or(false);
    }

    @Override
    public boolean isComponent(Class<?> type) {
        return type.isAnnotationPresent(Service.class);
    }
}
