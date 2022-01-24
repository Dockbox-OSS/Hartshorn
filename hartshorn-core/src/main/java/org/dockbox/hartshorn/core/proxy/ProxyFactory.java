package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import java.lang.reflect.Method;

public interface ProxyFactory<T, F extends ProxyFactory<T, F>> {

    F delegate(T delegate);

    <S> F delegate(Class<S> type, S delegate);

    F delegate(MethodContext<?, T> method, T delegate);

    F delegate(Method method, T delegate);

    F intercept(MethodContext<?, T> method, MethodInterceptor<T> interceptor);

    F intercept(Method method, MethodInterceptor<T> interceptor);

    Exceptional<T> proxy() throws ApplicationException;

    Class<T> type();
}
