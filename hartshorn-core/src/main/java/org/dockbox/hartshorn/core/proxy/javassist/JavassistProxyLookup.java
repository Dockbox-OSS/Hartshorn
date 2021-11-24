package org.dockbox.hartshorn.core.proxy.javassist;

import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.ProxyLookup;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class JavassistProxyLookup implements ProxyLookup {

    @Override
    public <T> Class<T> unproxy(final T instance) {
        final MethodHandler methodHandler = ProxyFactory.getHandler((javassist.util.proxy.Proxy) instance);
        if (methodHandler instanceof ProxyHandler proxyHandler) {
            return proxyHandler.type().type();
        }
        return instance != null ? (Class<T>) instance.getClass() : null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return instance != null && this.isProxy(instance.getClass());
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return ProxyFactory.isProxyClass(candidate);
    }
}
