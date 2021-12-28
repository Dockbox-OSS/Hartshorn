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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.lang.annotation.Annotation;

public abstract class FunctionalComponentPostProcessor<A extends Annotation> implements ComponentPostProcessor<A> {

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return ComponentPostProcessor.super.preconditions(context, key, instance)
                && context.locator().container(key.type()).get().componentType() == ComponentType.FUNCTIONAL;
    }
}
