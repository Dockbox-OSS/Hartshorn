package org.dockbox.hartshorn.core.proxy;

public interface StateAwareProxyFactory<T, F extends ProxyFactory<T, F>> extends ProxyFactory<T, F> {
    StateAwareProxyFactory<T, F> trackState(boolean trackState);

    boolean modified();
}
