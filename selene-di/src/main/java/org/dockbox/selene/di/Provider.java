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

package org.dockbox.selene.di;

import org.dockbox.selene.di.properties.InjectorProperty;

import java.util.function.Consumer;

public class Provider {

    public static <T> T provide(Class<T> type, InjectorProperty<?>... additionalProperties) {
        return InjectableBootstrap.getInstance().getInstance(type, additionalProperties);
    }

    public static <T> T provide(Class<T> type, Object... varargs) {
        return provide(type, SeleneFactory.use(varargs));
    }

    /**
     * Run with module.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param consumer
     *         the consumer
     */
    public static <T> void with(Class<T> type, Consumer<T> consumer) {
        T instance = Provider.provide(type);
        if (null != instance) consumer.accept(instance);
    }
    
}
