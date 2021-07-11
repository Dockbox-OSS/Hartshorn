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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.proxy.handle.ProxyHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleProxyContext implements ProxyContext {

    private final Method proceed;
    private final ProxyHolder holder;
    private final Object self;

    @Override
    public <T> T invoke(Object... args) throws ApplicationException {
        try {
            //noinspection unchecked
            return (T) this.getProceed().invoke(this.getSelf(), args);
        }
        catch (InvocationTargetException | IllegalAccessException | ClassCastException e) {
            throw new ApplicationException(e);
        }
    }
}
