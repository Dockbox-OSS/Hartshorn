/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.proxy.advice;

import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.ProxyObject;
import org.dockbox.hartshorn.proxy.advice.intercept.CustomInvocation;
import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.invoke.MethodHandle;

/**
 * Standard implementation of {@link ProxyMethodInterceptHandler} that uses reflection to invoke methods on the target
 * instance. Certain optimizations may be applied to improve performance, such as caching of {@link MethodHandle}s.
 *
 * @param <T> the type of the target instance
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class ReflectionProxyMethodInterceptHandler<T> implements ProxyMethodInterceptHandler<T>, ProxyObject<T> {

    private final ProxyMethodInvoker<T> methodInvoker;
    private final ProxyManager<T> manager;

    public ReflectionProxyMethodInterceptHandler(ProxyMethodInterceptor<T> interceptor) {
        this.methodInvoker = new ReflectionProxyMethodInvoker<>(interceptor);
        this.manager = interceptor.manager();
    }

    @Override
    public Object handleNonInterceptedMethod(T self, MethodInvokable source, Invokable proxy, T callbackTarget, Object[] arguments) throws Throwable {
        Option<?> delegate = this.manager()
                .advisor()
                .resolver()
                .method(source.toMethod())
                .delegate();

        if (delegate.present()) {
            return this.handleDelegateMethod(delegate.get(), callbackTarget, source, arguments);
        }
        else {
            return this.handleNonDelegateMethod(self, callbackTarget, source, proxy, arguments);
        }
    }

    @Override
    public Object handleInterceptedMethod(MethodInvokable source, T callbackTarget, CustomInvocation<?> customInvocation, Object[] arguments, MethodInterceptor<T, Object> interceptor) throws Throwable {
        return this.methodInvoker.invokeInterceptor(callbackTarget,
                TypeUtils.unchecked(source.toIntrospector(), MethodView.class),
                arguments,
                interceptor,
                TypeUtils.unchecked(customInvocation, CustomInvocation.class)
        );
    }

    @Override
    public ProxyMethodInvoker<T> methodInvoker() {
        return this.methodInvoker;
    }

    protected Object handleDelegateMethod(Object delegate, T self, Invokable source, Object[] args) throws Throwable {
        Option<Object> defaultMethod = this.tryInvokeDefaultMethod(self, source, args);
        if (defaultMethod.present()) {
            return defaultMethod.get();
        }

        Object result = source.invoke(delegate, args);
        if (result == delegate) {
            return self;
        }
        return result;
    }

    protected Object handleNonDelegateMethod(T self, T callbackTarget, Invokable source, Invokable proxy, Object[] args) throws Throwable {
        Option<Object> defaultMethod = this.tryInvokeDefaultMethod(self, source, args);
        if (defaultMethod.present()) {
            return defaultMethod.get();
        }

        Object result;
        if (callbackTarget == self && proxy != null) {
            result = proxy.invoke(callbackTarget, args);
        }
        else {
            result = this.methodInvoker.invokeReal(self, source, source, args);
        }
        return result;
    }

    /**
     * Attempts to invoke a default {@link Object} method, such as {@link Object#equals(Object)}, {@link Object#hashCode()}
     * or {@link Object#toString()}. This will delegate to the respective methods in {@link ProxyObject} to allow for
     * custom implementations.
     *
     * @param self the instance on which the method is invoked
     * @param target the method that is invoked
     * @param args the arguments that are passed to the method
     * @return the result of the invocation, if the method is a default method
     */
    protected Option<Object> tryInvokeDefaultMethod(T self, Invokable target, Object[] args) {
        return Option.of(() -> {
            if (this.isEqualsMethod(target)){
                return this.proxyEquals(args[0]);
            }
            if (this.isToStringMethod(target)) {
                return this.proxyToString(self);
            }
            if (this.isHashCodeMethod(target)) {
                return this.proxyHashCode(self);
            }

            throw new UnsupportedOperationException("Unsupported default method: " + target.qualifiedName());
        });
    }

    @Override
    public ProxyManager<T> manager() {
        return this.manager;
    }
}
