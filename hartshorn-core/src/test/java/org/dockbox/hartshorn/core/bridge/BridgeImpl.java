package org.dockbox.hartshorn.core.bridge;

import org.dockbox.hartshorn.core.annotations.service.Service;

@Service
public interface BridgeImpl extends BridgeParent<Bob>{
    @Override
    Bob bridgeMethod();
}
