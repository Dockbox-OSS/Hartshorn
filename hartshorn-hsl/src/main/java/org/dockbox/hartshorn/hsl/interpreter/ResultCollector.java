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

package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.util.Result;

/**
 * Collector to optionally save return values/results which have been calculated inside
 * an expression or script.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public interface ResultCollector {

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
     * Gets the global result from the stack. If no global result exists {@link Result#empty()}
     * is returned.
     * @return The result value, or {@link Result#empty()}.
     * @param <T> The type of the result.
     */
    <T> Result<T> result();

    /**
     * Gets a result with the given ID. If no result with the given ID exists
     * {@link Result#empty()} is returned.
     * @param id The ID of the result.
     * @return The result value, or {@link Result#empty()}.
     * @param <T> The type of the result.
     */
    <T> Result<T> result(String id);

    /**
     * Removes all stored results from the collector instance. If no results exist
     * nothing happens.
     */
    void clear();
}
