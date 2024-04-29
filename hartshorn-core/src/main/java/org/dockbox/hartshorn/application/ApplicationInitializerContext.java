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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.util.AbstractSingleElementContext;
import org.dockbox.hartshorn.util.SingleElementContext;

/**
 * A context used to initialize the application. This context will always provide a {@link DefaultBindingConfigurerContext}
 * as the first element, which can be used to configure the {@link DefaultBindingConfigurer} used to create the application's
 * default bindings.
 *
 * @param <I> The type of the input object.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public class ApplicationInitializerContext<I> extends AbstractSingleElementContext<I> {

    public ApplicationInitializerContext(I input) {
        super(input);
    }

    /**
     * Initializes the context with a {@link DefaultBindingConfigurerContext} as the first element.
     *
     * @return The current context.
     */
    public ApplicationInitializerContext<I> initializeInitial() {
        this.addContext(new DefaultBindingConfigurerContext());
        return this;
    }

    @Override
    protected <T> SingleElementContext<T> clone(T input) {
        return new ApplicationInitializerContext<>(input);
    }
}
