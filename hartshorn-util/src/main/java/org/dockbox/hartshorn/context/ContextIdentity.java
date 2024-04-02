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
 * A context identity is a key that can be used to identify a context. It can be used to
 * look up a context in a {@link ContextView} instance, or to create a new context instance.
 *
 * <p>Note that a context identity is not a context itself. It is only a key that can be
 * used to reach a usable context.
 *
 * @param <T> The type of the context.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public interface ContextIdentity<T extends ContextView> {

    /**
     * Gets the type of the context represented by this identity.
     * @return The type of the context represented by this identity.
     */
    Class<T> type();

    /**
     * Gets the name of the context represented by this identity. This is null if no name was defined.
     * If the context represented by this identity is a {@link NamedContext}, it is not ensured that
     * the name of the context is equal to the name of the identity. This is only a recommendation.
     *
     * @return The name of the context represented by this identity.
     */
    String name();

    /**
     * Indicates whether the fallback function of this identity requires an active application to
     * be present. If there is no explicit fallback function, this will always return true.
     *
     * @return {@code true} if the fallback function of this key requires an active application.
     */
    boolean requiresApplicationContext();

    /**
     * Creates a new context value for this key. If a fallback function is present, it will be used
     * to create the value.
     *
     * @return The newly created context value.
     * @throws IllegalStateException If no fallback function is present to create the value.
     */
    T create();

}
