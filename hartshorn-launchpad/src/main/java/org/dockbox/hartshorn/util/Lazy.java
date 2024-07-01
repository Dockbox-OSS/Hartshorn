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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.ComponentKey;

/**
 * A lazy wrapper for a component. The component is only resolved when {@link #get()} is called. The component is
 * resolved using the {@link ApplicationContext}.
 *
 * @param <T> the type of the component
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 *
 * @deprecated Not recommended for use, instead use {@link org.dockbox.hartshorn.component.ComponentProvider}s
 *             when you need to request components lazily.
 */
@Deprecated(since = "0.6.0", forRemoval = true)
public final class Lazy<T> {

    private final ApplicationContext applicationContext;
    private final ComponentKey<T> type;
    private T value;

    private Lazy(ApplicationContext applicationContext, ComponentKey<T> type) {
        this.applicationContext = applicationContext;
        this.type = type;
    }

    /**
     * Creates a new lazy wrapper for the given component type.
     *
     * @param applicationContext the application context to use for resolving the component
     * @param type the type of the component
     * @param <T> the type of the component
     *
     * @return a new lazy wrapper
     */
    public static <T> Lazy<T> of(ApplicationContext applicationContext, Class<T> type) {
        return new Lazy<>(applicationContext, ComponentKey.of(type));
    }

    /**
     * Creates a new lazy wrapper for the given component key.
     *
     * @param applicationContext the application context to use for resolving the component
     * @param type the type of the component
     * @param <T> the type of the component
     *
     * @return a new lazy wrapper
     */
    public static <T> Lazy<T> of(ApplicationContext applicationContext, ComponentKey<T> type) {
        return new Lazy<>(applicationContext, type);
    }

    /**
     * Creates a new lazy wrapper for the given component instance. This does not require an application context,
     * as the component is already resolved.
     *
     * @param type the type of the component
     * @param instance the component instance
     * @param <T> the type of the component
     *
     * @return a new lazy wrapper
     */
    public static <T> Lazy<T> ofInstance(Class<T> type, T instance) {
        Lazy<T> lazy = of(null, type);
        lazy.value = instance;
        return lazy;
    }

    /**
     * Gets the component instance. If the component has not yet been resolved, it will be resolved using the
     * {@link ApplicationContext}.
     *
     * @return the component instance
     */
    public T get() {
        if (this.value == null) {
            this.value = this.applicationContext.get(this.type);
        }
        return this.value;
    }
}
