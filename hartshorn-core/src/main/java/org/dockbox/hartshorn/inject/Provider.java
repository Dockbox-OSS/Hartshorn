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

import java.util.function.Function;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A provider is a class that can provide an instance of a {@link ComponentKey} binding. The
 * provider is not always responsible for creating the instance, but it can be used to create
 * the instance if it is not available.
 *
 * @param <T> The type instance to provide.
 * @author Guus Lieben
 * @since 0.4.3
 */
public sealed interface Provider<T> permits TypeAwareProvider, NonTypeAwareProvider, ComposedProvider {

    /**
     * Provides an instance of the {@link ComponentKey} binding. The {@link ApplicationContext} can be used
     * to retrieve required dependencies for the instance. The context should not be used to enable
     * the instance, or populate fields of the instance.
     *
     * @param context        The {@link ApplicationContext} to use.
     * @param requestContext
     * @return The instance, if it can be created.
     */
    Option<ObjectContainer<T>> provide(ApplicationContext context, ComponentRequestContext requestContext) throws ApplicationException;

    /**
     * Maps the result of this provider using the provided {@link Function}. The result of the
     * function is returned as the result of the provider. This may return a new provider, or
     * this provider.
     *
     * @param mappingFunction The function to apply to the result of this provider.
     * @return A provider that applies the provided function to the result of this provider.
     */
    default Provider<T> map(Function<ObjectContainer<T>, ObjectContainer<T>> mappingFunction) {
        return new ComposedProvider<>(this, mappingFunction);
    }

    /**
     * Indicates the default lifecycle of components provided by this provider. This is used to
     * determine whether the result of this provider should be cached, and whether this provider
     * may be called multiple times. Note that it remains up to the container to decide whether
     * to respect this lifecycle.
     *
     * @return The default lifecycle of components provided by this provider.
     */
    LifecycleType defaultLifecycle();

    /**
     * Indicates the default laziness of components provided by this provider. This is used to
     * determine whether the result of this provider should be created eagerly, or lazily. Note
     * that it remains up to the container to decide whether to respect this laziness.
     *
     * <p>If the implementation of this provider does not support laziness, this method should
     * return {@link Tristate#UNDEFINED}, so the container can decide whether to eagerly or
     * lazily create components.
     *
     * @return The default laziness of components provided by this provider.
     */
    Tristate defaultLazy();
}
