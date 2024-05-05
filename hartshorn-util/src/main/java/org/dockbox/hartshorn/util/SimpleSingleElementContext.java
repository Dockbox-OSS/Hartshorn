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

/**
 * A simple implementation of {@link SingleElementContext} which can be used for simple
 * single element contexts.
 *
 * @param <I> the type of the input
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SimpleSingleElementContext<I> extends AbstractSingleElementContext<I> {

    protected SimpleSingleElementContext(@NonNull I input) {
        super(input);
    }

    /**
     * Creates a new {@link SimpleSingleElementContext} with the given input. This is a
     * convenience method to better align with the common use of {@link SingleElementContext}.
     *
     * @param input the input value
     * @param <I> the type of the input
     * 
     * @return the new context
     */
    public static <I> SimpleSingleElementContext<I> create(@NonNull I input) {
        return new SimpleSingleElementContext<>(input);
    }

    @Override
    protected <T> SingleElementContext<T> clone(@NonNull T input) {
        return new SimpleSingleElementContext<>(input);
    }
}
