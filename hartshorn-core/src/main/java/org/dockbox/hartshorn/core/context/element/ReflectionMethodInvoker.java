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

package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.domain.Exceptional;

public class ReflectionMethodInvoker<T, P> implements MethodInvoker<T, P> {

    @Override
    public Exceptional<T> invoke(final MethodContext<T, P> method, final P instance, final Object[] args) {
        final Exceptional<T> result = Exceptional.of(() -> (T) method.method().invoke(instance, args));
        if (result.caught()) {
            Throwable cause = result.error();
            if (result.error().getCause() != null) cause = result.error().getCause();
            return Exceptional.of(result.orNull(), cause);
        }
        return result;
    }
}