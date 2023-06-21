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

package org.dockbox.hartshorn.component.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentType;

public abstract class FunctionalComponentPostProcessor extends ComponentPostProcessor {

    @Override
    public <T> boolean isCompatible(final ComponentProcessingContext<T> processingContext) {
        final ComponentContainer<?> container = processingContext.get(ComponentKey.of(ComponentContainer.class));
        return container.componentType() == ComponentType.FUNCTIONAL;
    }

    /**
     * @deprecated This method is deprecated and will be removed in a future release. Instead use
     * {@link #preConfigureComponent(ApplicationContext, Object, ComponentProcessingContext)},
     * {@link #postConfigureComponent(ApplicationContext, Object, ComponentProcessingContext)} or
     * {@link #initializeComponent(ApplicationContext, Object, ComponentProcessingContext)}
     */
    @Deprecated(forRemoval = true, since = "23.1")
    public <T> T process(final ApplicationContext context, @Nullable final T instance, final ComponentContainer<?> container, final ComponentProcessingContext<T> processingContext) {
        throw new UnsupportedOperationException("This method is deprecated and will be removed in a future release");
    }
}
