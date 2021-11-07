package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.proxy.DelegatorAccessor;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

@Service
interface AccessorFactory {
    @Factory
    <T> DelegatorAccessor<T> delegatorAccessor(ProxyHandler<T> handler);
}
