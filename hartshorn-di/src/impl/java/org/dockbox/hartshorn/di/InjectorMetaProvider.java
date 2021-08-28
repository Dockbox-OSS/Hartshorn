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
import org.dockbox.hartshorn.api.domain.TypedOwner;
import org.dockbox.hartshorn.api.domain.TypedOwnerImpl;
import org.dockbox.hartshorn.di.annotations.component.Component;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.MethodContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.services.ComponentContainer;

import javax.inject.Singleton;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InjectorMetaProvider implements MetaProvider {

    private final ApplicationContext context;

    @Override
    public TypedOwner lookup(final TypeContext<?> type) {
        final Exceptional<Entity> annotated = type.annotation(Entity.class);
        if (annotated.present()) {
            return TypedOwnerImpl.of(annotated.get().name());
        }
        else {
            final Exceptional<ComponentContainer> container = this.context.locator().container(type);
            if (container.present()) {
                final ComponentContainer service = container.get();
                if (!service.owner().isVoid()) return this.lookup(service.owner());
                else {
                    if (!"".equals(service.id())) return TypedOwnerImpl.of(service.id());
                }
            }
        }
        return TypedOwnerImpl.of(Bindings.serviceId(this.context, type));
    }

    @Override
    public boolean singleton(final TypeContext<?> type) {
        if (type.annotation(Singleton.class).present()) return true;

        return this.context.locator()
                .container(type)
                .map(ComponentContainer::singleton)
                .or(false);
    }

    @Override
    public boolean singleton(final MethodContext<?, ?> method) {
        return method.annotation(Singleton.class).present();
    }

    @Override
    public boolean isComponent(final TypeContext<?> type) {
        return type.annotation(Component.class).present();
    }
}
