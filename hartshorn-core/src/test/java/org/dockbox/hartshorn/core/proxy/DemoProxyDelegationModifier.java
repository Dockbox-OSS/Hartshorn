package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.services.ProxyDelegationModifier;

public class DemoProxyDelegationModifier extends ProxyDelegationModifier<AbstractProxyParent, Service> {
    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    protected Class<AbstractProxyParent> parentTarget() {
        return AbstractProxyParent.class;
    }
}
