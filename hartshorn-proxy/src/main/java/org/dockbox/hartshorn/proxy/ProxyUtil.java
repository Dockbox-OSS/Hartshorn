package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.proxy.handle.ProxyHandler;
import org.dockbox.hartshorn.proxy.handle.ProxyInterfaceHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class ProxyUtil {

    public static <T> ProxyHandler<T> handler(Class<T> type, T instance) {
        ProxyHandler<T> handler = null;
        if (instance != null) {
            if (ProxyFactory.isProxyClass(instance.getClass())) {
                final MethodHandler methodHandler = ProxyFactory.getHandler((javassist.util.proxy.Proxy) instance);
                if (methodHandler instanceof ProxyHandler proxyHandler) {
                    //noinspection unchecked
                    handler = (ProxyHandler<T>) proxyHandler;
                }
            }
            else if (Proxy.isProxyClass(instance.getClass())) {
                final InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
                if (invocationHandler instanceof ProxyInterfaceHandler proxyInterfaceHandler) {
                    //noinspection unchecked
                    handler = proxyInterfaceHandler.handler();
                }
            }
        }

        if (handler == null) handler = new ProxyHandler<>(instance, type);
        return handler;
    }

}
