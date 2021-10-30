package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.context.ContextCarrier;

public class ContextCarrierDelegationModifier extends ProxyDelegationModifier<ContextCarrier, Service> {
    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    protected Class<ContextCarrier> parentTarget() {
        return ContextCarrier.class;
    }
}
