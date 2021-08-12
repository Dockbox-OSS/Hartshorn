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
import org.dockbox.hartshorn.di.inject.InjectionModifier;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

public abstract class ServiceModifier<A extends Annotation> implements InjectionModifier<A> {

    public <T> boolean preconditions(final Class<T> type, @Nullable final T instance, final Attribute<?>... properties) {
        return ApplicationContextAware.instance().context().locator().container(type).present()
                && this.modifies(type, instance, properties);
    }

    protected abstract <T> boolean modifies(Class<T> type, @Nullable T instance, Attribute<?>... properties);

}
