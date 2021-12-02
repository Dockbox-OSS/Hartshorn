/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.domain.Exceptional;

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
     * {@link org.dockbox.hartshorn.core.annotations.context.AutoCreating}, it will be created using the provided
     * {@link ApplicationContext}.
     *
     * @param applicationContext The application context to use when creating the context.
     * @param context The type of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type.
     */
    <C extends Context> Exceptional<C> first(ApplicationContext applicationContext, Class<C> context);

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
