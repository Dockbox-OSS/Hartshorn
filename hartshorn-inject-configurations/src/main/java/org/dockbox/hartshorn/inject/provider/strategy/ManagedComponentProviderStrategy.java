package org.dockbox.hartshorn.inject.provider.strategy;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ComponentResolutionException;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.provider.ComponentObjectContainer;
import org.dockbox.hartshorn.inject.provider.ComponentRegistryAwareProviderOrchestrator;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.util.ApplicationException;

public class ManagedComponentProviderStrategy implements ComponentProviderStrategy {
    @Override
    public <T> ObjectContainer<T> get(ComponentKey<T> componentKey, ComponentRequestContext requestContext, ComponentProviderStrategyChain<T> chain) throws ComponentResolutionException, ApplicationException {
        if (chain.application().defaultProvider() instanceof ComponentRegistryAwareProviderOrchestrator componentRegistryAware) {
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
