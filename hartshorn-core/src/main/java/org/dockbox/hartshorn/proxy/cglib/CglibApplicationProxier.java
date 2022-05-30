package org.dockbox.hartshorn.proxy.cglib;

import org.dockbox.hartshorn.proxy.AbstractApplicationProxier;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;

public class CglibApplicationProxier extends AbstractApplicationProxier {

    @Override
    public <T> StateAwareProxyFactory<T, ?> factory(final Class<T> type) {
        return new CglibProxyFactory<>(type, this.applicationManager().applicationContext());
    }
}
