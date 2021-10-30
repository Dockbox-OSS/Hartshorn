package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.annotations.inject.Binds;

@Binds(AbstractProxyParent.class)
public class ConcreteProxyImplementation implements AbstractProxyParent {
    @Override
    public String name() {
        return "concrete";
    }

    @Override
    public int age() {
        return -1;
    }
}
