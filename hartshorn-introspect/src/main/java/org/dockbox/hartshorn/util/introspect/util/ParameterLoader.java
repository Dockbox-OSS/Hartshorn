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

package org.dockbox.hartshorn.util.introspect.util;

import java.util.List;

/**
 * A {@link ParameterLoader} is responsible for loading arguments for a specific context, often based on
 * a method or constructor. This serves as a standardized way to load arguments for various use-cases, such
 * as component injection, or event handling.
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface ParameterLoader {

    /**
     * Returns whether this loader is compatible with the provided context. If this method returns
     * {@code true}, you can safely proceed to invoke {@link #loadArgument(ParameterLoaderContext, int, Object...)}
     * or {@link #loadArguments(ParameterLoaderContext, Object...)} to load the parameter value(s).
     *
     * @param context the context to check
     * @return whether or not this loader is compatible with the provided context
     */
    boolean isCompatible(ParameterLoaderContext context);

    /**
     * Loads a specific argument for the provided context. This method should only be invoked after
     * {@link #isCompatible(ParameterLoaderContext)} has returned {@code true}. If the provided index
     * is out of bounds, {@code null} is returned.
     *
     * @param context the context to use when looking up the argument value
     * @param index the index of the argument to load
     * @param args the arguments that are passed to the method that is being invoked
     * @return the argument value, or {@code null} if the argument could not be loaded
     */
    Object loadArgument(ParameterLoaderContext context, int index, Object... args);

    /**
     * Loads all arguments for the provided context. This method should only be invoked after
     * {@link #isCompatible(ParameterLoaderContext)} has returned {@code true}.
     *
     * <p>If this loader is not compatible with the provided context, the provided arguments will
     * be returned as-is. If the provided context is compatible, but no arguments could be loaded,
     * {@code null} will be used at the index of the argument that could not be loaded.
     *
     * @param context the context to use when looking up the argument value
     * @param args the arguments that are passed to the method that is being invoked
     * @return the argument values, or {@code null} if an argument could not be loaded
     */
    List<Object> loadArguments(ParameterLoaderContext context, Object... args);
}
