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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.util.function.Function;

public final class InjectionPoint<T> {

    private final TypeContext<T> type;
    private final InjectFunction<T> point;

    private InjectionPoint(final TypeContext<T> type, final InjectFunction<T> point) {
        this.type = type;
        this.point = point;
    }

    public static <T> InjectionPoint<T> of(final TypeContext<T> type, final Function<T, T> point) {
        return new InjectionPoint<>(type, (instance, it) -> point.apply(instance));
    }

    public static <T> InjectionPoint<T> of(final TypeContext<T> type, final InjectFunction<T> point) {
        return new InjectionPoint<>(type, point);
    }

    public boolean accepts(final TypeContext<?> type) {
        return type.childOf(this.type);
    }

    public T apply(final T instance) {
        return this.apply(instance, TypeContext.of(instance));
    }

    public T apply(final T instance, final TypeContext<T> type) {
        return this.point.apply(instance, type);
    }
}
