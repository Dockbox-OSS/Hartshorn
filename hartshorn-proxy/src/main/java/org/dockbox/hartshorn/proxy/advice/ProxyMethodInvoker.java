/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.proxy.advice.intercept.CustomInvocation;
import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

/**
 * A helper interface that is commonly used in combination with {@link ProxyMethodInterceptHandler} to invoke methods on
 * an object. This interface is typically used to delegate method invocations to other objects, such as the real target
 * object, a delegate object, or an interceptor. This will ensure the method gets invoked correctly, supporting
 * concrete, abstract, and interface methods.
 *
 * @param <T> the type of the target object
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public interface ProxyMethodInvoker<T> {

    /**
     * Invokes the given {@code interceptor} for the given {@code source} method. The {@code interceptor} is expected to
     * return the result of the method invocation, or throw an exception.
     *
     * @param self the proxy instance on which the method is invoked
     * @param source the method that is invoked
     * @param args the arguments that are passed to the method
     * @param interceptor the interceptor that intercepts the method invocation
     * @param customInvocation the custom invocation that is used to invoke the real method if possible
     * @return the result of the method invocation
     * @param <R> the type of the result of the method invocation
     * @throws Throwable if an error occurs during the method invocation
     */
    <R> R invokeInterceptor(T self, MethodView<T, R> source, Object[] args, MethodInterceptor<T, R> interceptor, CustomInvocation<R> customInvocation) throws Throwable;

    /**
     * Invokes the given {@code target} method on the given {@code self} object. The {@code target} method is expected
     * to return the result of the method invocation, or throw an exception.
     *
     * @param self the proxy instance on which the method is invoked
     * @param target the method that is invoked
     * @param args the arguments that are passed to the method
     * @return the result of the method invocation
     */
    Object invokeDelegate(T self, Invokable target, Object[] args) throws Throwable;

    /**
     * Attempts to invoke the underlying non-proxied method for the current proxy. If the real method is not concrete,
     * or otherwise not available, this method will attempt to invoke the default stub method.
     *
     * @param self the proxy instance on which the method is invoked
     * @param source the real method that is invoked
     * @param target the proxied method that is invoked
     * @param args the arguments that are passed to the method
     * @return the result of the method invocation
     * @throws Throwable if an error occurs during the method invocation
     */
    Object invokeReal(T self, Invokable source, Invokable target, Object[] args) throws Throwable;
}
