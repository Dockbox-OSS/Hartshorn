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

import org.dockbox.hartshorn.util.ContextualCustomizer;

/**
 * A generic context used to store a {@link org.dockbox.hartshorn.util.Customizer}s. This allows any
 * context-driven customizations to be passed along to builders or similar constructs.
 *
 * @param <T> The type of the object that is being customized
 *     
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CustomizerContext<T> extends DefaultContext {

    private final ContextualCustomizer<T> customizer;

    public CustomizerContext(ContextualCustomizer<T> customizer) {
        this.customizer = customizer;
    }

    /**
     * Returns the {@link ContextualCustomizer} stored in this context.
     *
     * @return The contextual customizer
     */
    public ContextualCustomizer<T> customizer() {
        return this.customizer;
    }

    /**
     * Composes this context with another, returning a new context that contains both customizers.
     *
     * @param before The context to compose with
     * @return A new context containing both customizers
     */
    public CustomizerContext<T> compose(CustomizerContext<T> before) {
        return new CustomizerContext<>(this.customizer.compose(before.customizer()));
    }

    /**
     * Composes this context with another customizer, returning a new context that contains both
     * customizers.
     *
     * @param before The customizer to compose with
     * @return A new context containing both customizers
     */
    public CustomizerContext<T> compose(ContextualCustomizer<T> before) {
        return new CustomizerContext<>(this.customizer.compose(before));
    }
}
