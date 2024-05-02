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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.ComponentRequestContext;

/**
 * A component provider is a class that is capable of providing components. Components are identified using
 * {@link ComponentKey keys}. Components can be either managed or unmanaged. Managed components are typically
 * bound to an active {@link ApplicationContext} and are therefore available to all components in the application.
 * Unmanaged components are typically not explicitly registered, and may be treated as either injectable or
 * non-injectable, depending on the implementation.
 *
 * <p>Component providers are typically used to provide components to other components, and are therefore
 * typically injectable. However, they may also be used to provide components to non-injectable classes, such
 * as static classes. In this case, the provider will typically be obtained from a {@link ApplicationContext}
 * or another {@link ComponentProvider}.
 *
 * <p>Components may be provided from a configured scope, as defined in {@link ComponentKey#scope()}, or from
 * the default scope configured by the provider. The default scope is typically the same as the scope of the provider
 * itself, but this is not required.
 *
 * @see ComponentKey
 * @author Guus Lieben
 * @since 0.4.9
 */
public interface ComponentProvider {

    /**
     * Returns the component for the given key, using the request context to determine the
     * use case for the component. The request context may be used to provide additional
     * information to the provider, such as the current component that is being resolved.
     *
     * <p>Both parameters are used to determine the appropriate state of the returned
     * component. The {@link ComponentKey key} describes the component itself, whereas the
     * {@link ComponentRequestContext request context} describes the use case for the
     * component.
     *
     * <p>Note that singleton components are typically resolved using the default request
     * context, and ignore the given request context.
     *
     * @param key The key of the component to return.
     * @param requestContext The request context to use for resolving the component.
     *
     * @return The component for the given key.
     *
     * @param <T> The type of the component to return.
     */
    <T> T get(ComponentKey<T> key, ComponentRequestContext requestContext);

    /**
     * Returns the component for the given key.
     *
     * @param key The key of the component to return.
     * @param <T> The type of the component to return.
     *
     * @return The component for the given key.
     */
    default <T> T get(ComponentKey<T> key) {
        return this.get(key, ComponentRequestContext.createForComponent());
    }

    /**
     * Returns the component for the given type and name metadata. If {@code named} is null, the given
     * {@link Class} is used to identify the component.
     *
     * @param type The type of the component to return.
     * @param qualifiers The metadata of the component to return.
     * @param <T> The type of the component to return.
     *
     * @return The component for the given type and name metadata.
     *
     * @deprecated Use {@link #get(ComponentKey)} instead.
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    default <T> T get(Class<T> type, QualifierKey<?>... qualifiers) {
        ComponentKey<T> key = ComponentKey.builder(type).qualifiers(qualifiers).build();
        return this.get(key);
    }

    /**
     * Returns the component for the given type.
     *
     * @param type The type of the component to return.
     * @param <T> The type of the component to return.
     *
     * @return The component for the given type.
     */
    default <T> T get(Class<T> type) {
        ComponentKey<T> key = ComponentKey.of(type);
        return this.get(key);
    }

    /**
     * Returns the component for the given type, using the request context to determine the
     * use case for the component. The request context may be used to provide additional
     * information to the provider, such as the current component that is being resolved.
     *
     * <p>Both parameters are used to determine the appropriate state of the returned
     * component. The {@link Class type} describes the component itself, whereas the
     * {@link ComponentRequestContext request context} describes the use case for the
     * component.
     *
     * <p>Note that singleton components are typically resolved using the default request
     * context, and ignore the given request context.
     *
     * @param type The type of the component to return.
     * @param requestContext The request context to use for resolving the component.
     *
     * @return The component for the given type.
     *
     * @param <T> The type of the component to return.
     */
    default <T> T get(Class<T> type, ComponentRequestContext requestContext) {
        ComponentKey<T> key = ComponentKey.of(type);
        return this.get(key, requestContext);
    }

    Scope scope();
}
