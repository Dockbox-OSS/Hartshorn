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

/**
 * A context is a collection of objects that can be used to share data between different parts of the application. This
 * is the interface for any context which is capable of storing other contexts.
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
public interface Context extends ContextView {

    /**
     * Adds the given context to the current context.
     *
     * @param context The context to add.
     * @param <C> The type of the context.
     */
    <C extends ContextView> void addContext(C context);

    /**
     * Adds the given context as a named context using the given name.
     *
     * @param name The name of the context.
     * @param context The context to add.
     * @param <C> The type of the context.
     */
    <C extends ContextView> void addContext(String name, C context);

    /**
     * Returns a view of the current context. This view is read-only and does not allow
     * for modification of the context.
     *
     * @return A view of the current context.
     */
    ContextView contextView();
}
