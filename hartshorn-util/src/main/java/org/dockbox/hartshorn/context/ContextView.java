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

package org.dockbox.hartshorn.context;

import java.util.List;

import org.dockbox.hartshorn.util.option.Option;

/**
 * Immutable view of a {@link Context}, providing read-only access to the contexts stored within.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface ContextView {

    /**
     * Returns all contexts stored in the current context.
     * @return All contexts stored in the current context.
     */
    List<ContextView> contexts();

    /**
     * Returns the first context of the given type.
     *
     * @param context The type of the context.
     * @param <C> The type of the context.
     * @return The first context of the given type.
     */
    <C extends ContextView> Option<C> firstContext(Class<C> context);

    /**
     * Returns all contexts of the given type. If no contexts of the given type are found, an empty list is returned.
     *
     * @param context The type of the context.
     * @return All contexts of the given type.
     * @param <C> The type of the context.
     */
    <C extends ContextView> List<C> contexts(Class<C> context);

    /**
     * Returns the first context matching the given identity. If no context is found, an attempt may be made to create
     * a new context using the fallback function of the identity. If no fallback function is present, or it is not
     * compatible with the current context, an empty option is returned.
     *
     * @param key The identity of the context.
     * @return The first context matching the given identity.
     * @param <C> The type of the context.
     */
    <C extends ContextView> Option<C> firstContext(ContextIdentity<C> key);

    /**
     * Returns all contexts matching the given identity. If no contexts are found, an empty list is returned.
     *
     * @param key The identity of the context.
     * @return All contexts matching the given identity.
     * @param <C> The type of the context.
     */
    <C extends ContextView> List<C> contexts(ContextIdentity<C> key);

    /**
     * Copies all child contexts from the current context to the given context. This will not copy the current context
     * itself.
     *
     * @param context The context to copy to.
     */
    void copyToContext(Context context);
}
