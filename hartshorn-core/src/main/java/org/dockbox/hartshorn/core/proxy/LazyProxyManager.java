package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.TypeMap;

import java.lang.reflect.Method;
import java.util.Map;

public class LazyProxyManager<T> implements DelegateProxyManager<T> {

    private final Class<T> proxyClass;
    private final Class<T> targetClass;
    private T proxy;

    private final Map<Method, ?> delegates;
    private final TypeMap<Object> typeDelegates;
    private final Map<Method, MethodInterceptor> interceptors;
    private final T delegate;

    public LazyProxyManager(final Class<T> proxyClass, final Class<T> targetClass, final T delegate, final Map<Method, ?> delegates, final TypeMap<Object> typeDelegates,
                            final Map<Method, MethodInterceptor> interceptors) {
        // TODO: ApplicationContext to validate incoming values
        this.proxyClass = proxyClass;
        this.targetClass = targetClass;
        this.delegate = delegate;
        this.delegates = delegates;
        this.typeDelegates = typeDelegates;
        this.interceptors = interceptors;
    }

    public void proxy(final T proxy) {
        if (this.proxy != null) {
            throw new IllegalStateException("Proxy already set");
        }
        this.proxy = proxy;
    }

    @Override
    public Class<T> targetClass() {
        return this.targetClass;
    }

    @Override
    public Class<T> proxyClass() {
        return this.proxyClass;
    }

    @Override
    public T proxy() {
        return this.proxy;
    }

    @Override
    public Exceptional<T> delegate() {
        return Exceptional.of(this.delegate);
    }

    @Override
    public Exceptional<T> delegate(final Method method) {
        return Exceptional.of(this.delegates).map(map -> map.get(method)).map(delegate -> (T) delegate);
    }

    @Override
    public <S> Exceptional<S> delegate(final Class<S> type) {
        return Exceptional.of(this.typeDelegates).map(map -> map.get(type)).map(type::cast);
    }

    @Override
    public Exceptional<MethodInterceptor<T>> interceptor(final Method method) {
        return Exceptional.of(this.interceptors).map(map -> map.get(method));
    }
}
