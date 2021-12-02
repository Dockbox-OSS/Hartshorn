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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.services.FactoryServiceModifier;
import org.dockbox.hartshorn.core.services.FactoryServiceProcessor;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The context used by the {@link FactoryServiceProcessor} and {@link FactoryServiceModifier}. This context keeps track
 * of associated constructors for {@link org.dockbox.hartshorn.core.annotations.Factory} methods. If no constructor
 * exists for a given method, an exception will be thrown by this context.
 */
@AutoCreating
public class FactoryContext extends DefaultContext {

    private final Map<MethodContext<?, ?>, ConstructorContext<?>> bounds = HartshornUtils.emptyConcurrentMap();

    /**
     * Associates a constructor with a method.
     *
     * @param method The method to associate the constructor with.
     * @param constructor The constructor to associate with the method.
     * @param <T> The return type of the method.
     */
    public <T> void register(final MethodContext<T, ?> method, final ConstructorContext<T> constructor) {
        this.bounds.put(method, constructor);
    }

    /**
     * Returns the constructor associated with the given method. If no constructor is associated with the method, an
     * exception will be thrown.
     *
     * @param method The method to get the constructor for.
     * @param <T> The return type of the method.
     * @return The constructor associated with the given method.
     * @throws NoSuchElementException If no constructor is associated with the method.
     */
    public <T> ConstructorContext<T> get(final MethodContext<T, ?> method) {
        final ConstructorContext<?> constructor = this.bounds.get(method);
        if (constructor == null) throw new NoSuchElementException("No bound constructor present for method " + method.qualifiedName());
        return (ConstructorContext<T>) constructor;
    }
}
