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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.MethodContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ProxyMethodCallbackImpl<T> implements ProxyMethodCallback<T> {

    @Getter final CallbackPhase phase;
    final ProxyCallback<T> function;

    @Override
    public void accept(final MethodContext<?, T> method, final T instance, final Object[] args, final ProxyContext context) {
        this.function.accept(method, instance, args, context);
    }
}