package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.AnnotationHelper.AnnotationInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class NativeProxyLookup implements ProxyLookup {

    @Override
    public <T> Class<T> unproxy(final T instance) {
        final InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
        if (invocationHandler instanceof JavaInterfaceProxyHandler proxyInterfaceHandler) {
            return proxyInterfaceHandler.handler().type().type();
        }
        else if (invocationHandler instanceof AnnotationInvocationHandler annotationInvocationHandler) {
            return (Class<T>) annotationInvocationHandler.annotation().annotationType();
        }
        else if (instance instanceof Annotation annotation) {
            return (Class<T>) annotation.annotationType();
        }
        return instance != null ? (Class<T>) instance.getClass() : null;
    }

    @Override
    public boolean isProxy(final Object instance) {
        return instance != null && this.isProxy(instance.getClass());
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return Proxy.isProxyClass(candidate);
    }
}
