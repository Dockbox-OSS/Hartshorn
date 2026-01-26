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

public final class ContextUtilities {

    private ContextUtilities() {
    }

    public static <C extends Context> Option<C> first(
            Iterable<Context> contexts,
            Class<C> type
    ) {
        return first(contexts, new SimpleContextIdentity<>(type));
    }

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

    public static <C extends Context> Option<C> first(
            Context[] contexts,
            Class<C> type
    ) {
        return first(contexts, new SimpleContextIdentity<>(type));
    }

    public static <C extends Context> Option<C> first(
            Context[] contexts,
            ContextIdentity<C> identity
    ) {
        return first(List.of(contexts), identity);
    }
}
