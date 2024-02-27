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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A factory for creating component instances. This can be used by {@link Binder binders} to create instances
 * of components when they are requested, but no custom provider is available.
 *
 * @author Guus Lieben
 * @since 0.4.11
 */
public interface ComponentInstanceFactory {

    /**
     * Creates a new instance of the component identified by the given key. The key may be named or unnamed, and
     * may contain a scope. Depending on the implementation, these attributes may be used to determine the
     * instance that is created.
     *
     * @param <T>            The type of the component
     * @param key            The key of the component to create
     * @param requestContext
     * @return The created instance
     * @throws ApplicationException When the instance cannot be created
     */
    <T> Option<ObjectContainer<T>> instantiate(ComponentKey<T> key, ComponentRequestContext requestContext) throws ApplicationException;
}
