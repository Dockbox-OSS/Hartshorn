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

package org.dockbox.hartshorn.context;

import java.util.List;

import org.dockbox.hartshorn.util.option.Option;

/**
 * A context is a collection of objects that can be used to share data between different parts of the application. This
 * is the interface for any context which is capable of storing other contexts.
 *
 * @author Guus Lieben
 * @since 0.4.1
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
    <C extends Context> Option<C> first(Class<C> context);

    /**
     * Returns all contexts of the given type. If no contexts of the given type are found, an empty list is returned.
     *
     * @param context The type of the context.
     * @return All contexts of the given type.
     * @param <C> The type of the context.
     */
    <C extends Context> List<C> all(Class<C> context);

    /**
     * Returns the first context matching the given identity. If no context is found, an attempt may be made to create
     * a new context using the fallback function of the identity. If no fallback function is present, or it is not
     * compatible with the current context, an empty option is returned.
     *
     * @param key The identity of the context.
     * @return The first context matching the given identity.
     * @param <C> The type of the context.
     */
    <C extends Context> Option<C> first(ContextIdentity<C> key);

    /**
     * Returns all contexts matching the given identity. If no contexts are found, an empty list is returned.
     *
     * @param key The identity of the context.
     * @return All contexts matching the given identity.
     * @param <C> The type of the context.
     */
    <C extends Context> List<C> all(ContextIdentity<C> key);

    /**
     * Copies all child contexts from the current context to the given context. This will not copy the current context
     * itself.
     *
     * @param context The context to copy to.
     */
    void copyTo(Context context);
}
