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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import lombok.Getter;

/**
 * Context type for {@link org.dockbox.hartshorn.core.proxy.ProxyHandler}s to use when storing backing implementation
 * instances. Each backing implementation is stored based on its class, for example an instance of
 * {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessorImpl} will be stored under the key
 * {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessor}.
 */
@Getter
@AutoCreating
public class BackingImplementationContext extends DefaultContext {

    private final Map<Class<?>, Object> implementations = new ConcurrentHashMap<>();

    /**
     * Gets the backing implementation instance for the given class, if it exists.
     * @param type The class to get the backing implementation instance for.
     * @param <P> The type of the backing implementation instance.
     * @return The backing implementation instance for the given class, if it exists.
     */
    public <P> Exceptional<P> get(final Class<P> type) {
        return Exceptional.of(() -> (P) this.implementations.get(type));
    }

    /**
     * Gets the backing implementation instance for the given class, if it exists. If it doesn't exist, it will be
     * created using the given function.
     * @param key The class to get the backing implementation instance for.
     * @param mappingFunction The function to create the backing implementation instance if it doesn't exist.
     * @param <P> The type of the backing implementation instance.
     * @return The backing implementation instance for the given class.
     */
    public <P> P computeIfAbsent(final Class<P> key, @NonNull final Function<? super Class<P>, P> mappingFunction) {
        return (P) this.implementations.computeIfAbsent(key, (Function<? super Class<?>, ?>) mappingFunction);
    }
}
