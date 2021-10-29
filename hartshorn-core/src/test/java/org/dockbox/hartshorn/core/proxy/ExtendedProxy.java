package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.annotations.service.Service;

@Service
public abstract class ExtendedProxy implements AbstractProxyParent {
    @Override
    public int age() {
        return 21;
    }
}
