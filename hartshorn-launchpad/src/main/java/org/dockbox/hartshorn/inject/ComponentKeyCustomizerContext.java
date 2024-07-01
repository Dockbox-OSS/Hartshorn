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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentKey.Builder;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.ContextualCustomizer;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;

/**
 * A context used to store a {@link ContextualCustomizer} for a {@link ComponentKey.Builder}. This allows
 * any context-driven customizations to be applied to the builder when the component is registered. This
 * context is not specific to any implementation, but is typically used for {@link ParameterLoader}s or
 * {@link ParameterLoaderRule}s.
 *
 * @see Builder
 * @see ParameterLoader
 * @see ParameterLoaderRule
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ComponentKeyCustomizerContext extends DefaultContext {

    private final ContextualCustomizer<ComponentKey.Builder<?>> customizer;

    public ComponentKeyCustomizerContext(ContextualCustomizer<ComponentKey.Builder<?>> customizer) {
        this.customizer = customizer;
    }

    /**
     * Returns the {@link ContextualCustomizer} stored in this context. This customizer can be applied to
     * a {@link Builder} to customize the builder.
     *
     * @return The contextual customizer
     */
    public ContextualCustomizer<ComponentKey.Builder<?>> customizer() {
        return this.customizer;
    }
}
