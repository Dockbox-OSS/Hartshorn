/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util;

import java.util.function.Function;

import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link StringTypeAdapter} implementation. This implementation uses a {@link Function} to convert the {@link String}
 * to the desired type. This is useful when the conversion is not trivial, so that the converter can be defined in a
 * field instead of requiring it to be implemented it in a separate class.
 *
 * @param <T> The type to convert to.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public final class StringTypeAdapterImpl<T> implements StringTypeAdapter<T> {

    private final Class<T> type;
    private final Function<String, Option<T>> function;

    private StringTypeAdapterImpl(Class<T> type, Function<String, Option<T>> function) {
        this.type = type;
        this.function = function;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public Option<T> adapt(String value) {
        return this.function.apply(value);
    }

    /**
     * Creates a new {@link StringTypeAdapterImpl} instance.
     *
     * @param type The type to convert to.
     * @param function The function to use to convert the {@link String} to the desired type.
     * @param <T> The type to convert to.
     * @return The new {@link StringTypeAdapterImpl} instance.
     */
    public static <T> StringTypeAdapterImpl<T> of(Class<T> type, Function<String, Option<T>> function) {
        return new StringTypeAdapterImpl<>(type, function);
    }
}
