package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.context.ContextCarrier;

public interface ProxyMethodInterceptor<T> extends ContextCarrier {

    ProxyManager<T> manager();

    Object intercept(Object self, MethodInvokable source, Invokable proxy, Object[] args) throws Throwable;

    Object[] resolveArgs(MethodInvokable method, Object instance, Object[] args);

}
