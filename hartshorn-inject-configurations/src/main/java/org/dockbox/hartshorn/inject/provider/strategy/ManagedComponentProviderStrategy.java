/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.inject.provider.strategy;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.provider.ComponentObjectContainer;
import org.dockbox.hartshorn.inject.provider.ComponentRegistryAwareComponentProvider;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.util.ApplicationException;

/**
 * A {@link ComponentProviderStrategy} which redirects the component request to the global provider if the request
 * is for a component that is also present in the {@link ComponentRegistry}. If the {@link
 * InjectionCapableApplication#defaultProvider()} is not {@link ComponentRegistryAwareComponentProvider aware of
 * the component registry}, no redirects will take place.
 *
 * <p>This strategy ensures that managed components are always handled by the global context.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ManagedComponentProviderStrategy implements ComponentProviderStrategy {

    @Override
    public <T> ObjectContainer<T> get(ComponentKey<T> componentKey, ComponentRequestContext requestContext, ComponentProviderStrategyChain<T> chain) throws ComponentResolutionException, ApplicationException {
        if (chain.application().defaultProvider() instanceof ComponentRegistryAwareComponentProvider componentRegistryAware) {
            ComponentRegistry componentRegistry = componentRegistryAware.componentRegistry();
            if (componentRegistry.container(componentKey.type()).present()) {
                // Only redirect if the request is for another scope than the application scope. If we're already
                // providing a component in the application scope, we can continue with the chain as usual.
                boolean isScopedRequest = componentKey.scope()
                        .map(Scope::installableScopeType)
                        .test(key -> {
                            ScopeKey applicationScope = chain.application().defaultProvider().scope().installableScopeType();
                            return !key.equals(applicationScope);
                        });
                if (isScopedRequest) {
                    ComponentKey<T> rescopedKey = componentKey.mutable()
                            .scope(chain.application().defaultProvider().scope())
                            .build();

                    // Redirect outside the chain to ensure that the component is resolved with the correct scope.
                    T instance = chain.application().defaultProvider().get(rescopedKey);
                    ComponentObjectContainer<T> container = ComponentObjectContainer.ofSingleton(instance);
                    container.processed(true);
                    return container;
                }
            }
        }
        return chain.get(componentKey, requestContext);
    }
}
