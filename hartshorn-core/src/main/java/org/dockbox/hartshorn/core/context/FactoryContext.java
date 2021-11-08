/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;

import java.util.Map;
import java.util.NoSuchElementException;

@AutoCreating
public class FactoryContext extends DefaultContext {
    private final Map<MethodContext<?, ?>, ConstructorContext<?>> bounds = HartshornUtils.emptyConcurrentMap();

    public <T> void register(final MethodContext<T, ?> method, final ConstructorContext<T> constructor) {
        this.bounds.put(method, constructor);
    }

    public <T> ConstructorContext<T> get(final MethodContext<T, ?> method) {
        final ConstructorContext<?> constructor = this.bounds.get(method);
        if (constructor == null) throw new NoSuchElementException("No bound constructor present for method " + method.qualifiedName());
        return (ConstructorContext<T>) constructor;
    }
}
