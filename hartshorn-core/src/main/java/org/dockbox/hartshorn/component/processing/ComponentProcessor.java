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
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.inject.Provider;

import java.lang.annotation.Annotation;

/**
 * The {@link ComponentProcessor} is a service that can be used to process components, whether
 * that is during the initialization of the framework, or after a component has been created.
 *
 * <p>This interface defines the basic contract for a component processor. All processors should
 * have an activator annotation, and a phase at which they are performed.
 *
 * @author Guus Lieben
 * @since 22.1
 * @param <A> The activator annotation type.
 */
public interface ComponentProcessor<A extends Annotation> extends ActivatorFiltered<A>, OrderedComponentProcessor {

    /**
     * Processes a given component. The given <code>instance</code> may be null, if the component could not
     * be created through regular {@link Provider providers}. The given
     * {@link Key} will always be valid, and will always contain a valid {@link TypeContext}. If the instance
     * is present, the {@link TypeContext} will represent either the type of the component, or a parent of the
     * type of the component.
     *
     * <p>The provided {@link ApplicationContext} will always be valid. This is the context which is
     * responsible for the creation of the component.
     *
     * @param context The application context.
     * @param key The key of the component.
     * @param instance The instance of the component.
     * @param <T> The type of the component.
     *
     * @return The processed component.
     */
    <T> T process(ApplicationContext context, Key<T> key, @Nullable T instance);

    default <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        return this.process(context, key, instance);
    }

    /**
     * Determines whether the component processor should be called for the given component. By default,
     * the processor will only process components that are known to the application's {@link ComponentLocator}
     * through the active {@link ComponentContainer} of the component.
     *
     * @param context The application context.
     * @param key The key of the component.
     * @param instance The instance of the component.
     * @param <T> The type of the component.
     * @return True if the processor should be called, false otherwise.
     */
    default <T> boolean preconditions(final ApplicationContext context, final Key<T> key, @Nullable final T instance, final ComponentProcessingContext processingContext) {
        return processingContext.get(Key.of(ComponentContainer.class)) != null && this.modifies(context, key, instance, processingContext);
    }

    /**
     * Determines whether the component processor should be called for the given component. This method
     * will only be called if the preconditions of {@link #preconditions(ApplicationContext, Key, Object, ComponentProcessingContext)}
     * are met, assuming the {@link #preconditions(ApplicationContext, Key, Object, ComponentProcessingContext)} are not modified
     * by the implementing class.
     *
     * @param <T>
     *         The type of the component.
     * @param context
     *         The application context.
     * @param key
     *         The key of the component.
     * @param instance
     *         The instance of the component.
     * @param processingContext
     *
     * @return <code>true</code> if the component processor modifies the component, <code>false</code>
     *         otherwise.
     */
    <T> boolean modifies(ApplicationContext context, Key<T> key, @Nullable T instance, final ComponentProcessingContext processingContext);

}
