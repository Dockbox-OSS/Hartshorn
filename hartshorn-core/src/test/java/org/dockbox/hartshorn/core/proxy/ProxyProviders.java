package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.processing.Provider;

@Service
public class ProxyProviders {

    @Provider
    public InterfaceProxy proxy() {
        return new ConcreteProxy();
    }

}
