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

package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.launchpad.context.ApplicationContextCarrier;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Collector to optionally save return values/results which have been calculated inside
 * an expression or script.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface ResultCollector extends ApplicationContextCarrier {

    /**
     * Adds a global result to the stack, if a global result already exists it will be
     * overwritten.
     * @param value The result value.
     */
    void addResult(Object value);

    /**
     * Adds a result to the stack under the given ID. If a result with the given ID
     * already exists it will be overwritten.
     * @param id The ID to use when saving the result.
     * @param value The result value.
     */
    void addResult(String id, Object value);

    /**
     * Gets the global result from the stack. If no global result exists {@link Option#empty()}
     * is returned.
     *
     * @param type The type of the result.
     * @param <T> The type of the result.
     *
     * @return The result value, or {@link Option#empty()}.
     */
    <T> Option<T> result(Class<T> type);

    Option<?> result();

    /**
     * Gets a result with the given ID. If no result with the given ID exists
     * {@link Option#empty()} is returned.
     *
     * @param id The ID of the result.
     * @param type The type of the result.
     *
     * @return The result value, or {@link Option#empty()}.
     *
     * @param <T> The type of the result.
     */
    <T> Option<T> result(String id, Class<T> type);

    Option<?> result(String id);

    /**
     * Removes all stored results from the collector instance. If no results exist
     * nothing happens.
     */
    void clear();
}
