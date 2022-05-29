/*
 * Copyright 2019-2022 the original author or authors.
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
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.inject.Key;

/**
 * A component pre-processor is responsible for pre-processing a component. This can be used to
 * validate the component before it is added to the application context, or to add additional
 * information to the component before it is created.
 *
 * <p>The component post processor will only process a component if it is known to the application, which
 * is validated through the active {@link ComponentLocator}. If no {@link ComponentContainer} exists for
 * the component, the component post processor will not be called.
 *
 * <p>To specify when a component post processor should be active, and when the application should ignore
 * it, the processor will always require an activator annotation to be specified as type parameter
 * <code>A</code>. If the specified annotation is not annotated with {@link ServiceActivator}, it is up to
 * the active {@link ApplicationContext} to decide whether this should be considered an error or not.
 *
 * <p>The component pre-processor will be called for each component that is added to the application.
 * It is executed during application construction.
 *
 * @author Guus Lieben
 * @since 22.1
 */
public non-sealed interface ComponentPreProcessor extends ComponentProcessor {

    @Override
    default <T> boolean modifies(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        throw new UnsupportedOperationException("Component pre-processor does not support instance processing, use modifies(ApplicationContext, Key) instead.");
    }

    @Override
    default <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        throw new UnsupportedOperationException("Component pre-processor does not support instance processing, use process(ApplicationContext, Key) instead.");
    }

    /**
     * Determines whether the component pre-processor modifies the component. As component instances will
     * not exist yet, this method does not expect the <code>instance</code> to be specified.
     *
     * @param context The application context.
     * @param key The type context of the component.
     * @return <code>true</code> if the component pre-processor modifies the component, <code>false</code>
     * otherwise.
     * @see ComponentProcessor#modifies(ApplicationContext, Key, Object, ComponentProcessingContext)
     * @see ComponentProcessor#preconditions(ApplicationContext, Key, Object, ComponentProcessingContext)
     */
    boolean modifies(ApplicationContext context, Key<?> key);

    /**
     * Processes a given component. As component instances will not exist yet, this method does not expect
     * the <code>instance</code> to be specified.
     *
     * @param context The application context.
     * @param key The type context of the component.
     * @param <T> The type of the component.
     * @see ComponentProcessor#process(ApplicationContext, Key, Object)
     */
    <T> void process(ApplicationContext context, Key<T> key);
}
