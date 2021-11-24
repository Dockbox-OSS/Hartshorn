package org.dockbox.hartshorn.core.proxy;

public interface ProxyLookup {

    <T> Class<T> unproxy(T instance);

    boolean isProxy(Object instance);
    boolean isProxy(Class<?> candidate);

}
