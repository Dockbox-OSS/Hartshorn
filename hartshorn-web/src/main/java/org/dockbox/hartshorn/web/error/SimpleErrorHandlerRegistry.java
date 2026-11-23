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

package org.dockbox.hartshorn.web.error;

import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple implementation of {@link ErrorHandlerRegistry}, capable of resolving nearest-type
 * exception handlers if required.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleErrorHandlerRegistry implements ErrorHandlerRegistry {

    private final Map<
            Class<? extends Throwable>,
            RequestErrorHandler<? extends Throwable>
            > handlers = new ConcurrentHashMap<>();

    @Override
    public <E extends Throwable> void register(
            Class<E> exceptionType,
            RequestErrorHandler<E> handler
    ) {
        this.handlers.put(exceptionType, handler);
    }

    @Override
    public Option<RequestErrorHandler<? extends Throwable>> getHandler(
            Class<? extends Throwable> exceptionType
    ) {
        // If there's an exact match, always prefer that
        if (this.handlers.containsKey(exceptionType)) {
            return Option.of(this.handlers.get(exceptionType));
        }
        // Type-match for nearest compatible handler
        return Option.of(this.handlers.keySet().stream()
                .filter(handlerType -> handlerType.isAssignableFrom(exceptionType))
                .collect(Collectors.toMap(
                        handlerType -> handlerType,
                        handlerType -> this.distance(exceptionType, handlerType)
                ))
                .entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(this.handlers::get));
    }

    /**
     * The distance in type hierarchy between the target (requested type) and source (in registry).
     * -1 if the source is not assignable from the target (source is not a parent of target).
     *
     * @param targetType The target type (requested type)
     * @param sourceType The source type (in registry)
     * @return The distance in type hierarchy, or -1 if not assignable
     */
    private int distance(
            Class<? extends Throwable> targetType,
            Class<? extends Throwable> sourceType
    ) {
        if (!sourceType.isAssignableFrom(targetType)) {
            return -1;
        }
        int distance = 0;
        Class<?> current = targetType;
        while (current != null && !current.equals(sourceType)) {
            current = current.getSuperclass();
            distance++;
        }
        return distance;
    }
}
