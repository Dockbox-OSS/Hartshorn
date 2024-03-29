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

package org.dockbox.hartshorn.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Generic type reference, allowing for generic type reading. This is derived
 * from Jackson's TypeReference.
 *
 * @param <T> The generic type
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public abstract class GenericType<T> implements Comparable<GenericType<T>> {

    protected final Type type;

    protected GenericType() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("GenericType constructed without actual type information");
        }
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    /**
     * Returns the type of the generic type.
     *
     * @return the type of the generic type
     */
    public Type type() {
        return this.type;
    }

    /**
     * If this generic type represents a class, returns the class.
     *
     * @return the class, if this generic type represents a class
     */
    public Option<Class<T>> asClass() {
        if (this.type() instanceof Class<?> clazz) {
            return Option.of((Class<T>) clazz);
        }
        return Option.empty();
    }

    @Override
    public int compareTo(@NonNull GenericType<T> other) {
        return 0;
    }

}
