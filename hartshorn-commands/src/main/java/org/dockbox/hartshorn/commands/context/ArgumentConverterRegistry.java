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

import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public interface ArgumentConverterRegistry {

    /**
     * Indicates if any converter with the given {@code key} is registered.
     *
     * @param key The key to use during lookup
     *
     * @return {@code true} if a converter exists, or else {@code false}
     */
    boolean hasConverter(String key);

    /**
     * Gets the converter associated with the registered {@code key}, if it exists.
     *
     * @param key The key to use during lookup
     *
     * @return The converter if it exists, or {@link Option#empty()}
     */
    Option<ArgumentConverter<?>> converter(String key);

    /**
     * Indicates if any registered converter is able to convert into the given {@code type}.
     *
     * @param type The type the converter should convert into.
     *
     * @return {@code true} if a converter exists, or else {@code false}
     */
    boolean hasConverter(TypeView<?> type);

    /**
     * Gets the (first) converter which is able to convert into the given {@code type}, if it
     * exists.
     *
     * @param type The type the converter should convert into.
     * @param <T> The type parameter of the type
     *
     * @return The converter if it exists, or {@link Option#empty()}
     */
    <T> Option<ArgumentConverter<T>> converter(TypeView<T> type);
}
