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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.di.annotations.context.AutoCreating;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.Reflect;

import java.util.Map;

import lombok.Getter;

/**
 * The utility class which keeps track of all registered {@link ArgumentConverter argument converters}.
 */
@AutoCreating
public final class ArgumentConverterContext extends DefaultContext {

    @Getter private final transient Map<String, ArgumentConverter<?>> converterMap = HartshornUtils.emptyConcurrentMap();

    /**
     * Indicates if any converter with the given <code>key</code> is registered.
     * @param key The key to use during lookup
     * @return <code>true</code> if a converter exists, or else <code>false</code>
     */
    public boolean hasConverter(final String key) {
        return this.converter(key).present();
    }

    /**
     * Indicates if any registered converter is able to convert into the given <code>type</code>.
     * @param type The type the converter should convert into.
     * @return <code>true</code> if a converter exists, or else <code>false</code>
     */
    public boolean hasConverter(final Class<?> type) {
        return this.converter(type).present();
    }

    /**
     * Gets the converter associated with the registered <code>key</code>, if it exists.
     * @param key The key to use during lookup
     * @return The converter if it exists, or {@link Exceptional#empty()}
     */
    public Exceptional<ArgumentConverter<?>> converter(final String key) {
        return Exceptional.of(this.converterMap.get(key.toLowerCase()));
    }

    /**
     * Gets the (first) converter which is able to convert into the given <code>type</code>, if it
     * exists.
     * @param type The type the converter should convert into.
     * @param <T> The type parameter of the type
     * @return The converter if it exists, or {@link Exceptional#empty()}
     */
    public <T> Exceptional<ArgumentConverter<T>> converter(final Class<T> type) {
        //noinspection unchecked
        return Exceptional.of(this.converterMap.values().stream()
                .filter(converter -> Reflect.assigns(converter.type(), type))
                .map(converter -> (ArgumentConverter<T>) converter)
                .findFirst());
    }

    /**
     * Registers the given {@link ArgumentConverter} to the current context.
     * @param converter The converter to register
     */
    public void register(final ArgumentConverter<?> converter) {
        for (String key : converter.keys()) {
            key = key.toLowerCase();
            if (this.converterMap.containsKey(key))
                Hartshorn.log().warn("Duplicate argument key '" + key + "' found while registering converter, overwriting existing converter");
            this.converterMap.put(key, converter);
        }
    }
}
