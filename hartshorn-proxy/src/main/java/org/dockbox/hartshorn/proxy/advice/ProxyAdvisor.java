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

import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallbackContext;

/**
 * A callback interface for a proxy method. This interface is used to provide a callback mechanism for proxy methods,
 * while remaining unaware of which phase of the proxy method invocation is currently being executed. This is useful for
 * {@link org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor}s and is typically used to handle the
 * invocation of configured advisors or otherwise delegate to the next callback in the chain.
 *
 * @param <T> the type of the proxy method
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public interface ProxyAdvisor<T> {

    /**
     * Returns the resolver that can be used to determine which advisors have been applied to the proxy instance.
     *
     * @return the resolver
     */
    ProxyAdvisorResolver<T> resolver();

    /**
     * Executes the given {@link ProxyInterceptFunction} in a safe manner, while ensuring any configured advisors are
     * applied to the proxy method invocation. The given {@link ProxyInterceptFunction} is executed in the context of
     * the given {@link ProxyCallbackContext}, which allows the function to continue the execution chain.
     *
     * @param context the context of the proxy method invocation
     * @param interceptFunction the function to execute
     * @return the result of the function
     * @param <U> the type of the result
     * @throws Throwable any exception that is thrown by the function
     */
    <U> U safeWrapIntercept(ProxyCallbackContext<T> context, ProxyInterceptFunction<U> interceptFunction) throws Throwable;

}
