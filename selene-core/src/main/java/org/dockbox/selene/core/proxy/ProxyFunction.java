package org.dockbox.selene.core.proxy;

public interface ProxyFunction<T, R> {

    R delegate(T instance, Object[] args, ProxyHolder holder);
}
