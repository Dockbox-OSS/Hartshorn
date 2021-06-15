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

import org.dockbox.hartshorn.api.domain.MetaProvider;
import org.dockbox.hartshorn.di.annotations.Service;

import javax.inject.Singleton;

public abstract class InjectorMetaProvider implements MetaProvider {

    @Override
    public boolean isSingleton(Class<?> type) {
        if (type.isAnnotationPresent(Singleton.class)) return true;
        if (type.isAnnotationPresent(com.google.inject.Singleton.class)) return true;

        boolean serviceSingleton = false;
        if (type.isAnnotationPresent(Service.class)) {
            serviceSingleton = type.getAnnotation(Service.class).singleton();
        }
        return serviceSingleton;
    }
}
