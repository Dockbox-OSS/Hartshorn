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

package org.dockbox.hartshorn.util.introspect.util;

import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A rule that can be used to load a parameter value from a context. This is used by the
 * {@link RuleBasedParameterLoader} to load a collection of parameters from a context.
 *
 * @param <C> the context type
 *
 * @see RuleBasedParameterLoader
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public interface ParameterLoaderRule<C extends ParameterLoaderContext> {

    /**
     * Returns whether this rule accepts the provided parameter. If this method returns {@code true}, you
     * can safely proceed to invoke {@link #load(ParameterView, int, ParameterLoaderContext, Object...)} to
     * load the parameter value.
     *
     * @param parameter the parameter to check
     * @param index the index of the parameter in the parameter list
     * @param context the context to use when looking up the parameter value
     * @param args the arguments that are passed to the method that is being invoked
     * @return whether this rule accepts the provided parameter
     */
    boolean accepts(ParameterView<?> parameter, int index, C context, Object... args);

    /**
     * Loads the parameter value from the provided context. This method should only be invoked after
     * {@link #accepts(ParameterView, int, ParameterLoaderContext, Object...)} has returned {@code true}.
     *
     * <p>Implementations are expected to return an {@link Option} that contains the parameter value, or
     * {@link Option#empty()} if the parameter could not be loaded.
     *
     * @param parameter the parameter to load
     * @param index the index of the parameter in the parameter list
     * @param context the context to use when looking up the parameter value
     * @param args the arguments that are passed to the method that is being invoked
     * @return the parameter value, or {@link Option#empty()} if the parameter could not be loaded
     * @param <T> the type of the parameter
     */
    <T> Option<T> load(ParameterView<T> parameter, int index, C context, Object... args);
}
