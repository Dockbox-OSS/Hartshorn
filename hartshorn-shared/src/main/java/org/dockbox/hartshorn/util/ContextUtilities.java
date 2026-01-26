/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.NamedContext;
import org.dockbox.hartshorn.context.SimpleContextIdentity;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

/**
 * Utility methods for working with {@link Context contexts}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public final class ContextUtilities {

    private ContextUtilities() {
    }

    /**
     * Finds the first context of the given type in the provided iterable of contexts.
     *
     * @param contexts The iterable of contexts to search.
     * @param type The type of context to find.
     * @param <C> The type of context.
     *
     * @return An {@link Option} containing the first context of the given type, or an empty option
     * if no such context is found.
     */
    public static <C extends Context> Option<C> first(
            Iterable<Context> contexts,
            Class<C> type
    ) {
        return first(contexts, new SimpleContextIdentity<>(type));
    }

    /**
     * Finds the first context matching the given identity in the provided iterable of contexts.
     *
     * @param contexts The iterable of contexts to search.
     * @param identity The identity of the context to find.
     * @param <C> The type of context.
     *
     * @return An {@link Option} containing the first context matching the given identity, or an
     * empty option if no such context is found.
     */
    public static <C extends Context> Option<C> first(
            Iterable<Context> contexts,
            ContextIdentity<C> identity
    ) {
        for (Context context : contexts) {
            if (identity.type().isInstance(context)) {
                if (context instanceof NamedContext namedContext && identity.name() != null) {
                    if (identity.name().equals(namedContext.name())) {
                        return Option.of(identity.type().cast(context));
                    }
                }
                else {
                    return Option.of(identity.type().cast(context));
                }
            }
        }
        return Option.empty();
    }

    /**
     * Finds the first context of the given type in the provided array of contexts.
     *
     * @param contexts The array of contexts to search.
     * @param type The type of context to find.
     * @param <C> The type of context.
     *
     * @return An {@link Option} containing the first context of the given type, or an empty option
     * if no such context is found.
     */
    public static <C extends Context> Option<C> first(
            Context[] contexts,
            Class<C> type
    ) {
        return first(contexts, new SimpleContextIdentity<>(type));
    }

    /**
     * Finds the first context matching the given identity in the provided array of contexts.
     *
     * @param contexts The array of contexts to search.
     * @param identity The identity of the context to find.
     * @param <C> The type of context.
     *
     * @return An {@link Option} containing the first context matching the given identity, or an
     * empty option if no such context is found.
     */
    public static <C extends Context> Option<C> first(
            Context[] contexts,
            ContextIdentity<C> identity
    ) {
        return first(List.of(contexts), identity);
    }
}
