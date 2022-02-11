package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.reflect.Method;
import java.util.Set;

public interface ProxyManager<T> {
    Class<T> targetClass();

    Class<T> proxyClass();

    T proxy();

    Exceptional<T> delegate();

    Exceptional<T> delegate(Method method);

    <S> Exceptional<S> delegate(Class<S> type);

    Exceptional<MethodInterceptor<T>> interceptor(Method method);

    Set<MethodWrapper<T>> wrappers(Method method);
}
