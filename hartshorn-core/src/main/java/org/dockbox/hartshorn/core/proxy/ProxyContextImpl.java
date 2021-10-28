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

import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.context.element.MethodContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProxyContextImpl implements ProxyContext {

    private final MethodContext<?, ?> proceed;
    private final ProxyHolder holder;
    private final Object self;

    @Override
    public <T> T invoke(final Object... args) throws ApplicationException {
        try {
            if (this.proceed() == null || this.proceed().isAbstract()) return null;
            //noinspection unchecked
            return (T) ((MethodContext<?, Object>) this.proceed()).invoke(this.self(), args).orNull();
        }
        catch (final ClassCastException e) {
            throw new ApplicationException(e);
        }
    }
}
