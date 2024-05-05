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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.context.Context;

/**
 * A context that contains a single element. This is useful for when you want to pass a single
 * object through a context chain.
 *
 * @param <I> the type of the input object
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface SingleElementContext<I> extends Context {

    /**
     * Returns the input object. This is the object that is passed through the context chain, and
     * may be null.
     *
     * @return the input object
     */
    @NonNull I input();

    /**
     * Transforms the input into a new context. This is useful for when you want to keep the
     * same context that is attached to the input, but want to change the input itself. This
     * will return a new context with the new input, the original context will not be modified.
     *
     * @param input the new input
     * @return the new context, containing the new input and a copy of the original context
     * @param <T> the type of the new input
     */
    <T> @NonNull SingleElementContext<@Nullable T> transform(@NonNull T input);

}
