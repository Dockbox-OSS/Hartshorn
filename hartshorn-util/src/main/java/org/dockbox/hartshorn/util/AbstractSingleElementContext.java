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
import org.dockbox.hartshorn.context.DefaultContext;

/**
 * A basic context implementation that provides a single input object. This context is often used to initialize a
 * context chain, which is then used to create a new context with a different input object.
 *
 * @param <I> The type of the input object.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public abstract class AbstractSingleElementContext<I> extends DefaultContext implements SingleElementContext<I> {

    private final I input;

    protected AbstractSingleElementContext(@NonNull I input) {
        this.input = input;
    }

    @Override
    public @NonNull I input() {
        return this.input;
    }

    @Override
    public <T> @NonNull SingleElementContext<@Nullable T> transform(@NonNull T input) {
        SingleElementContext<T> clonedContext = this.clone(input);
        this.copyToContext(clonedContext);
        return clonedContext;
    }

    /**
     * Creates a clone of the current context with the provided input object. The clone will be a shallow copy of the
     * current context, with the exception of the input object. The clone will not contain any of the elements of the
     * current context.
     *
     * @param input The input object to use in the clone.
     * @return The cloned context.
     * @param <T> The type of the input object.
     */
    protected abstract <T> SingleElementContext<T> clone(@NonNull T input);
}
