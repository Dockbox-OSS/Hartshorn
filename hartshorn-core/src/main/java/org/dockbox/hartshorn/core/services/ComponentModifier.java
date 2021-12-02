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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.inject.InjectionModifier;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.annotation.Annotation;

public abstract class ComponentModifier<A extends Annotation> implements InjectionModifier<A> {

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance) {
        return context.locator().container(type).present() && this.modifies(context, type, instance);
    }

    protected abstract <T> boolean modifies(ApplicationContext context, TypeContext<T> type, @Nullable T instance);
}
