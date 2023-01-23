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

package org.dockbox.hartshorn.gradle.harness;

/**
 * Extension of {@link java.util.function.Consumer} and {@link java.util.function.BiConsumer} with
 * the addition of a third parameter of type <code>O</code>
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <O> the type of the third argument to the operation
 */
@FunctionalInterface
public interface TriConsumer<T, U, O> {
    void accept(T t, U u, O o);
}
