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

package org.dockbox.hartshorn.component.processing;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentType;

@Deprecated(since = "0.6.0", forRemoval = true)
public abstract class FunctionalComponentPostProcessor extends ComponentPostProcessor {

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        ComponentContainer<?> container = processingContext.get(ComponentKey.of(ComponentContainer.class));
        return container != null && container.componentType() == ComponentType.FUNCTIONAL;
    }

    /**
     * @param <T> The type of the component
     * @param context The application context
     * @param instance The component instance
     * @param container The component container
     * @param processingContext The processing context
     *
     * @return Nothing, this method is deprecated and will be removed in a future release
     *
     * @throws UnsupportedOperationException This method is deprecated and will be removed in a future release
     *
     * @deprecated This method is deprecated and will be removed in a future release. Instead use
     * {@link #preConfigureComponent(ApplicationContext, Object, ComponentProcessingContext)},
     * {@link #postConfigureComponent(ApplicationContext, Object, ComponentProcessingContext)} or
     * {@link #initializeComponent(ApplicationContext, Object, ComponentProcessingContext)}
     */
    @Deprecated(forRemoval = true, since = "0.5.0")
    public <T> T process(ApplicationContext context, @Nullable T instance, ComponentContainer<?> container, ComponentProcessingContext<T> processingContext) {
        throw new UnsupportedOperationException("This method is deprecated and will be removed in a future release");
    }
}
