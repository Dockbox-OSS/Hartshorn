package org.dockbox.hartshorn.proxy.cglib;

import org.dockbox.hartshorn.proxy.ProxyLookup;

public class CglibProxyLookup implements ProxyLookup {
    @Override
    public <T> Class<T> unproxy(final T instance) {
        return null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return false;
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return false;
    }
}
