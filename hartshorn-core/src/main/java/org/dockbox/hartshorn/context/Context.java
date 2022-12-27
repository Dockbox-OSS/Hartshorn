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

package org.dockbox.hartshorn.context;

import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

/**
 * A context is a collection of objects that can be used to share data between different parts of the application. This
 * is the interface for any context which is capable of storing other contexts.
 */
public interface Context {

    /**
     * Adds the given context to the current context.
     *
     * @param context The context to add.
     * @param <C> The type of the context.
     */
    <C extends Context> void add(C context);

    /**
     * Adds the given context as a named context using the given name.
     *
     * @param name The name of the context.
     * @param context The context to add.
     * @param <C> The type of the context.
     */
    <C extends Context> void add(String name, C context);

    /**
     * Returns all contexts stored in the current context.
     * @return All contexts stored in the current context.
     */
    List<Context> all();

    /**
     * Returns the first context of the given type.
     *
     * @param context The type of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type.
     */
    default <C extends Context> Option<C> first(final Class<C> context) {
        return this.first(ContextKey.of(context));
    }

    default <C extends Context> List<C> all(final Class<C> context) {
        return this.all(ContextKey.of(context));
    }

    <C extends Context> Option<C> first(ContextKey<C> key);

    <C extends Context> List<C> all(ContextKey<C> key);

}
