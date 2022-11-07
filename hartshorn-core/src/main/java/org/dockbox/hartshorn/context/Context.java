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

import org.dockbox.hartshorn.inject.Key;
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
     * Adds the given named context to the current context.
     *
     * @param context The context to add.
     * @param <N> The type of the context.
     */
    <N extends NamedContext> void add(N context);

    /**
     * Adds the given context as a named context using the given name.
     *
     * @param name The name of the context.
     * @param context The context to add.
     * @param <C> The type of the context.
     */
    <C extends Context> void add(String name, C context);

    /**
     * Returns the first context of the given name.
     *
     * @param name The name of the context.
     * @return The first context of the given name, if it exists.
     */
    Option<Context> first(String name);

    /**
     * Returns the first named context of the given named and type.
     *
     * @param name The name of the context.
     * @param context The type of the context.
     * @param <N> The type of the context.
     * @return The first named context of the given named and type, if it exists.
     */
    <N extends Context> Option<N> first(String name, Class<N> context);

    /**
     * Returns all contexts of the given type. If no contexts of the given type exist, an empty {@link List} will be
     * returned.
     *
     * @param context The type of the context.
     * @param <C> The type of the context.
     * @return All contexts of the given type.
     */
    <C extends Context> List<C> all(Class<C> context);

    /**
     * Returns all named contexts of the given type. If no named contexts of the given type exist, an empty {@link List}
     * will be returned.
     *
     * @param name The name of the context.
     * @return All named contexts of the given type.
     */
    List<Context> all(String name);

    /**
     * Returns all named contexts of the given named and type. If no named contexts of the given named and type exist,
     * an empty {@link List} will be returned.
     * @param name The name of the context.
     * @param context The type of the context.
     * @param <N> The type of the context.
     * @return All named contexts of the given named and type.
     */
    <N extends Context> List<N> all(String name, Class<N> context);

    /**
     * Returns the first context of the given type.
     *
     * @param context The type of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type.
     */
    <C extends Context> Option<C> first(Class<C> context);

    /**
     * Returns the first context of the given type and name.
     *
     * @param context The type of the context.
     * @param name The name of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type and name.
     */
    <C extends Context> Option<C> first(Class<C> context, String name);

    /**
     * Returns the first context of the given type and name, which are represented by the given key.
     *
     * @param context The key of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type and name.
     */
    <C extends Context> Option<C> first(Key<C> context);

}
