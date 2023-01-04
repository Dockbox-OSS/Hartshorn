/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.context.InstallIfAbsent;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;

/**
 * The utility class which keeps track of all registered {@link ArgumentConverter argument converters}.
 */
@InstallIfAbsent
public final class ArgumentConverterContext extends DefaultContext {

    private final transient Map<String, ArgumentConverter<?>> converterMap = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;

    @Inject
    public ArgumentConverterContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Indicates if any converter with the given <code>key</code> is registered.
     *
     * @param key The key to use during lookup
     *
     * @return <code>true</code> if a converter exists, or else <code>false</code>
     */
    public boolean hasConverter(final String key) {
        return this.converter(key).present();
    }

    /**
     * Gets the converter associated with the registered <code>key</code>, if it exists.
     *
     * @param key The key to use during lookup
     *
     * @return The converter if it exists, or {@link Option#empty()}
     */
    public Option<ArgumentConverter<?>> converter(final String key) {
        return Option.of(this.converterMap.get(key.toLowerCase()));
    }

    /**
     * Indicates if any registered converter is able to convert into the given <code>type</code>.
     *
     * @param type The type the converter should convert into.
     *
     * @return <code>true</code> if a converter exists, or else <code>false</code>
     */
    public boolean hasConverter(final TypeView<?> type) {
        return this.converter(type).present();
    }

    /**
     * Gets the (first) converter which is able to convert into the given <code>type</code>, if it
     * exists.
     *
     * @param type The type the converter should convert into.
     * @param <T> The type parameter of the type
     *
     * @return The converter if it exists, or {@link Option#empty()}
     */
    public <T> Option<ArgumentConverter<T>> converter(final TypeView<T> type) {
        return Option.of(this.converterMap.values().stream()
                .filter(converter -> type.isChildOf(converter.type()))
                .map(converter -> (ArgumentConverter<T>) converter)
                .findFirst());
    }

    /**
     * Registers the given {@link ArgumentConverter} to the current context.
     *
     * @param converter The converter to register
     */
    public void register(final ArgumentConverter<?> converter) {
        for (String key : converter.keys()) {
            key = key.toLowerCase();
            if (this.converterMap.containsKey(key))
                this.applicationContext.log().debug("Duplicate argument key '" + key + "' found while registering converter, overwriting existing converter.");
            this.converterMap.put(key, converter);
        }
    }
}
