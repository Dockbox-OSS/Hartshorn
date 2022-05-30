package org.dockbox.hartshorn.proxy.javassist;

import org.dockbox.hartshorn.proxy.AbstractApplicationProxier;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;

public class JavassistApplicationProxier extends AbstractApplicationProxier {

    @Override
    public <T> StateAwareProxyFactory<T, ?> factory(final Class<T> type) {
        return new JavassistProxyFactory<>(type, this.applicationManager().applicationContext());
    }
}
