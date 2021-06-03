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

import org.dockbox.selene.di.annotations.UseSingletonServices;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ServiceSingletonModifier extends ServiceModifier<UseSingletonServices> {

    private final Map<Class<?>, Object> instances = SeleneUtils.emptyConcurrentMap();

    @Override
    public <T> T process(ApplicationContext context, Class<T> type, @Nullable T instance, InjectorProperty<?>... properties) {
        if (!this.instances.containsKey(type)) {
            this.instances.put(type, instance);
        }
        //noinspection unchecked
        return (T) this.instances.get(type);
    }

    @Override
    public Class<UseSingletonServices> activator() {
        return UseSingletonServices.class;
    }

    @Override
    protected <T> boolean isModifiable(Class<T> type, @Nullable T instance, InjectorProperty<?>... properties) {
        return instance != null;
    }
}
