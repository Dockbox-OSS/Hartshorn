/*
 * Copyright 2019-2022 the original author or authors.
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

/**
 * A type adapter that converts a string to a potential value of type {@code T}. If the conversion fails,
 * {@link Exceptional#empty()} is returned.
 *
 * @param <T> The type to convert to
 * @author Guus Lieben
 * @since 21.9
 */
public interface StringTypeAdapter<T> {
    /**
     * Converts a string to a potential value of type {@code T}. If the conversion fails, {@link Exceptional#empty()} is
     * returned.
     *
     * @param value The string to convert
     * @return The converted value
     */
    Exceptional<T> adapt(String value);

    /**
     * Gets the target type of the current adapter.
     *
     * @return The target type
     */
    Class<T> type();
}