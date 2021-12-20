package org.dockbox.hartshorn.core.bridge;

import org.dockbox.hartshorn.core.annotations.Factory;

public interface BridgeParent<R> {
    @Factory
    R bridgeMethod();
}
