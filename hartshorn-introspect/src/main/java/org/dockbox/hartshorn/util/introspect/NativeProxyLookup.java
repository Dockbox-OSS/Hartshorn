package org.dockbox.hartshorn.util.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.annotations.PolymorphicAnnotationInvocationHandler;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A proxy lookup implementation that is capable of looking up native proxies. Native proxies are
 * proxies that are created through the standard Java API.
 *
 * @author Guus Lieben
 * @since 0.4.10
 */
public class NativeProxyLookup implements ProxyLookup {

    @Override
    public <T> Option<Class<T>> unproxy(final @NonNull T instance) {
        Class<T> unproxied = null;
        // Check if the instance is a proxy, as getInvocationHandler will yield an exception if it is not
        if(Proxy.isProxyClass(instance.getClass())) {
            final InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
            if(invocationHandler instanceof PolymorphicAnnotationInvocationHandler annotationInvocationHandler) {
                unproxied = TypeUtils.adjustWildcards(annotationInvocationHandler.annotation().annotationType(), Class.class);
            }
        }
        if(instance instanceof Annotation annotation) {
            unproxied = TypeUtils.adjustWildcards(annotation.annotationType(), Class.class);
        }
        return Option.of(unproxied);
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
