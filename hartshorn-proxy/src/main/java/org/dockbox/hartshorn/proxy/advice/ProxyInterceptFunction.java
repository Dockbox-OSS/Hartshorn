package org.dockbox.hartshorn.proxy.advice;

public interface ProxyInterceptFunction<T> {

    T handleInterception() throws Throwable;
}
