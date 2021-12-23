package org.dockbox.hartshorn.core.bridge;

import org.dockbox.hartshorn.core.annotations.stereotype.Service;

@Service
public interface BridgeImpl extends BridgeParent<Bob>{
    @Override
    Bob bridgeMethod();
}
