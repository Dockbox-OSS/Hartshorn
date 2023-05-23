package org.dockbox.hartshorn.proxy.advice;

import org.dockbox.hartshorn.proxy.advice.intercept.CustomInvocation;
import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

public interface ProxyMethodInvoker<T> {

    <R> R invokeInterceptor(T self, MethodView<T, R> source, Object[] args, MethodInterceptor<T, R> interceptor, CustomInvocation<R> customInvocation) throws Throwable;

    Object invokeDelegate(T self, Invokable target, Object[] args);

    Object invokeReal(T self, Invokable source, Invokable target, Object[] args) throws Throwable;
}
