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
 * A {@link Context} which uses {@link ContextKey}s to store and retrieve values instead
 * of {@link SimpleContextIdentity simple identities}. This allows for more flexibility
 * in the retrieval of values, as context keys support fallback functions and access to
 * the {@link org.dockbox.hartshorn.application.context.ApplicationContext}.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public interface ProvisionContext extends Context {

    @Override
    default <C extends ContextView> Option<C> firstContext(Class<C> context) {
        return this.firstContext(ContextKey.of(context));
    }

    @Override
    default <C extends ContextView> List<C> contexts(Class<C> context) {
        return this.contexts(ContextKey.of(context));
    }
}
