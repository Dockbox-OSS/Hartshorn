package org.dockbox.hartshorn.inject.provider.failure;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ComponentResolutionException;

public class ExceptionOnComponentResolutionFailureStrategy implements ComponentResolutionFailureStrategy {

    /**
     * A singleton instance of this strategy. This instance can be used to avoid unnecessary
     * object creation.
     */
    public static final ExceptionOnComponentResolutionFailureStrategy INSTANCE = new ExceptionOnComponentResolutionFailureStrategy();

    private ExceptionOnComponentResolutionFailureStrategy() {
        // Private constructor to enforce singleton pattern
    }

    @Override
    public <T> void onResolutionFailure(ComponentKey<T> componentKey, ComponentRequestContext requestContext) {
        throw new ComponentResolutionException("No component found for key " + componentKey);
    }
}
