package org.dockbox.hartshorn.inject.provider.failure;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;

public interface ComponentResolutionFailureStrategy {

    <T> void onResolutionFailure(ComponentKey<T> componentKey, ComponentRequestContext requestContext);
}
