package org.dockbox.hartshorn.proxy.advice;

import org.dockbox.hartshorn.proxy.advice.intercept.CustomInvocation;
import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;

public interface ProxyMethodInterceptHandler<T> {

    Object handleNonInterceptedMethod(T self, MethodInvokable source, Invokable proxy, T callbackTarget, Object[] arguments) throws Throwable;

    Object handleInterceptedMethod(MethodInvokable source, T callbackTarget, CustomInvocation<?> customInvocation, Object[] arguments, MethodInterceptor<T, Object> interceptor) throws Throwable;

    ProxyMethodInvoker<T> methodInvoker();
}
