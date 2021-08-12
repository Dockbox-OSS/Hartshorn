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
import org.dockbox.hartshorn.api.domain.TypedOwnerImpl;
import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.di.annotations.component.Component;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.util.Reflect;

import javax.inject.Singleton;
import javax.persistence.Entity;

public class InjectorMetaProvider implements MetaProvider {

    @Override
    public TypedOwner lookup(final Class<?> type) {
        final Exceptional<Entity> annotated = Reflect.annotation(type, Entity.class);
        if (annotated.present()) {
            return TypedOwnerImpl.of(annotated.get().name());
        }
        else {
            final Exceptional<ComponentContainer> container = ApplicationContextAware.instance().context().locator().container(type);
            if (container.present()) {
                final ComponentContainer service = container.get();
                if (Reflect.notVoid(service.owner())) return this.lookup(service.owner());
                else {
                    if (!"".equals(service.id())) return TypedOwnerImpl.of(service.id());
                }
            }
        }
        return TypedOwnerImpl.of(Bindings.serviceId(type));
    }

    @Override
    public boolean singleton(final Class<?> type) {
        if (Reflect.annotation(type, Singleton.class).present()) return true;

        return ApplicationContextAware.instance()
                .context()
                .locator()
                .container(type)
                .map(ComponentContainer::singleton)
                .or(false);
    }

    @Override
    public boolean component(final Class<?> type) {
        return Reflect.annotation(type, Component.class).present();
    }
}
