package org.dockbox.hartshorn.inject.provider.failure;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;

public class NoopComponentResolutionFailureStrategy implements ComponentResolutionFailureStrategy {

    public static final NoopComponentResolutionFailureStrategy INSTANCE = new NoopComponentResolutionFailureStrategy();

    private NoopComponentResolutionFailureStrategy() {
        // Private constructor to enforce singleton pattern
    }

    @Override
    public <T> void onResolutionFailure(ComponentKey<T> componentKey, ComponentRequestContext requestContext) {
        // No operation performed on resolution failure
    }
}
