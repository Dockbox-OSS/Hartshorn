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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Exceptional;

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
     * Returns the first context of the given type. If it doesn't exist, but the context is annotated with
     * {@link AutoCreating}, it will be created using the provided
     * {@link ApplicationContext}.
     *
     * @param applicationContext The application context to use when creating the context.
     * @param context The type of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type.
     */
    <C extends Context> Exceptional<C> first(ApplicationContext applicationContext, Class<C> context);

    /**
     * Returns the first context of the given type and name. If it doesn't exist, but the context is annotated with
     * {@link AutoCreating}, it will be created using the provided
     * {@link ApplicationContext}.
     *
     * @param applicationContext The application context to use when creating the context.
     * @param context The type of the context.
     * @param name The name of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type and name.
     */
    <C extends Context> Exceptional<C> first(ApplicationContext applicationContext, Class<C> context, String name);

    /**
     * Returns the first context of the given type and name, which are represented by the given key. If it doesn't exist,
     * but the context is annotated with {@link AutoCreating}, it will be
     * created using the provided {@link ApplicationContext}.
     *
     * @param applicationContext The application context to use when creating the context.
     * @param context The key of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type and name.
     */
    <C extends Context> Exceptional<C> first(ApplicationContext applicationContext, Key<C> context);

    /**
     * Returns the first context of the given name.
     *
     * @param name The name of the context.
     * @return The first context of the given name, if it exists.
     */
    Exceptional<Context> first(String name);

    /**
     * Returns the first named context of the given named and type.
     *
     * @param name The name of the context.
     * @param context The type of the context.
     * @param <N> The type of the context.
     * @return The first named context of the given named and type, if it exists.
     */
    <N extends Context> Exceptional<N> first(String name, Class<N> context);

    default <N extends Context> Exceptional<N> first(final String name, final TypeContext<N> context) {
        return this.first(name, context.type());
    }

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

}
