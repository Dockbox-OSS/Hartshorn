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

package org.dockbox.hartshorn.di.inject.wired;

import org.dockbox.hartshorn.di.ApplicationContextAware;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BeanWireContext<T, I extends T> implements WireContext<T, I> {

    private final Class<T> contract;
    private final Method bean;
    private final String name;

    @Override
    public T create(Object... arguments) throws ApplicationException {
        final Object service = ApplicationContextAware.instance().context().get(this.bean.getDeclaringClass());
        try {
            //noinspection unchecked
            return (T) this.bean.invoke(service, arguments);
        }
        catch (InvocationTargetException | IllegalAccessException | ClassCastException e) {
            throw new ApplicationException(e);
        }
    }
}
