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
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;

/**
 * A handler that can be used by {@link org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor}s to handle
 * method invocations. This handler is capable of handling invocations of methods that are not intercepted, as well as
 * methods that are intercepted. Typically, this handler is used in combination with a {@link ProxyMethodInvoker} which
 * can further delegate method invocations for various purposes.
 *
 * @param <T> the type of the target object
 *
 * @since 23.1
 * @author Guus Lieben
 */
public interface ProxyMethodInterceptHandler<T> {

    /**
     * Handles a method invocation that is not intercepted by an advisor.
     *
     * @param self the proxy instance on which the method is invoked
     * @param source the method that is invoked
     * @param proxy the proxy method that is invoked
     * @param callbackTarget the target object on which the method is invoked
     * @param arguments the arguments that are passed to the method
     * @return the result of the method invocation
     * @throws Throwable if an error occurs during the method invocation
     */
    Object handleNonInterceptedMethod(T self, MethodInvokable source, Invokable proxy, T callbackTarget, Object[] arguments) throws Throwable;

    /**
     * Handles a method invocation that is intercepted by the given {@link MethodInterceptor}.
     *
     * @param source the method that is invoked
     * @param callbackTarget the target object on which the method is invoked
     * @param customInvocation the custom invocation that is used to invoke the real method if possible
     * @param arguments the arguments that are passed to the method
     * @param interceptor the interceptor that intercepts the method invocation
     * @return the result of the method invocation
     * @throws Throwable if an error occurs during the method invocation
     */
    Object handleInterceptedMethod(MethodInvokable source, T callbackTarget, CustomInvocation<?> customInvocation, Object[] arguments, MethodInterceptor<T, Object> interceptor) throws Throwable;

    /**
     * Returns the {@link ProxyMethodInvoker} that is used by this handler.
     *
     * @return the method invoker
     */
    ProxyMethodInvoker<T> methodInvoker();
}
