/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.util.stream;

import java.util.stream.Gatherer;

/**
 * Utility methods for working with {@link Gatherer}s.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class StreamGatherers {

    private StreamGatherers() {
        // Utility class
    }

    /**
     * Filters elements by their type, only allowing elements of the specified type to pass through.
     * Resulting values are cast to the specified type.
     *
     * @param type the type to filter by
     * @param <T> the input type
     * @param <R> the output type
     *
     * @return a gatherer that filters elements by their type
     */
    public static <T, R> Gatherer<T, ?, R> filterByType(Class<R> type) {
        return Gatherer.of(
                (_, element, downstream) -> {
                    if (type.isInstance(element)) {
                        return downstream.push(type.cast(element));
                    }
                    return true;
                }
        );
    }
}
