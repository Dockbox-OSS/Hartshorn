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

package org.dockbox.hartshorn.util.function;

import java.util.concurrent.Callable;

/**
 * A utility functional interface which is similar to {@link Callable} or a {@link CheckedSupplier},
 * but pre-defines the exception type which may be thrown through a type parameter.
 *
 * @param <T> The type of the value
 * @param <E> The type of the exception
 *
 * @author Guus Lieben
 * @since 0.4.13
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

    /**
     * Gets a result.
     * @return a result
     * @throws E if an error occurs during the execution of the supplier
     */
    T get() throws E;
}
