package org.dockbox.hartshorn.core.proxy;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.TypeMap;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import java.lang.reflect.Method;
import java.util.Map;

public interface ProxyFactory<T, F extends ProxyFactory<T, F>> {

    F delegate(T delegate);

    <S> F delegate(Class<S> type, S delegate);

    F delegate(MethodContext<?, T> method, T delegate);

    F delegate(Method method, T delegate);

    F intercept(MethodContext<?, T> method, MethodInterceptor<T> interceptor);

    F intercept(Method method, MethodInterceptor<T> interceptor);

    F intercept(MethodContext<?, T> method, MethodWrapper<T> wrapper);

    F intercept(Method method, MethodWrapper<T> wrapper);

    Exceptional<T> proxy() throws ApplicationException;

    Class<T> type();

    @Nullable
    T typeDelegate();

    Map<Method, Object> delegates();

    Map<Method, MethodInterceptor<T>> interceptors();

    MultiMap<Method, MethodWrapper<T>> wrappers();

    TypeMap<Object> typeDelegates();
}
