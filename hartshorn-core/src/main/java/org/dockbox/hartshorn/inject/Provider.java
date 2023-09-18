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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.option.Option;

import java.util.function.Function;

/**
 * A provider is a class that can provide an instance of a {@link ComponentKey} binding. The
 * provider is not always responsible for creating the instance, but it can be used to create
 * the instance if it is not available.
 *
 * @param <T> The type instance to provide.
 * @author Guus Lieben
 * @since 0.4.3
 */
@FunctionalInterface
public interface Provider<T> {

    /**
     * Provides an instance of the {@link ComponentKey} binding. The {@link ApplicationContext} can be used
     * to retrieve required dependencies for the instance. The context should not be used to enable
     * the instance, or populate fields of the instance.
     *
     * @param context The {@link ApplicationContext} to use.
     * @return The instance, if it can be created.
     */
    Option<ObjectContainer<T>> provide(ApplicationContext context) throws ApplicationException;

    default Provider<T> map(Function<ObjectContainer<T>, ObjectContainer<T>> mappingFunction) {
        return new ComposedProvider<>(this, mappingFunction);
    }
}
