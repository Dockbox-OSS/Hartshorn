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

package org.dockbox.hartshorn.proxy.advice.intercept;

import org.dockbox.hartshorn.proxy.ProxyManager;

/**
 * A simple contract for intercepting method invocations. This contract can be used by
 * {@link org.dockbox.hartshorn.proxy.ProxyFactory proxy factories} to intercept method invocations.
 *
 * @param <T> the type of the proxy
 */
public interface ProxyMethodInterceptor<T> {

    /**
     * Returns the proxy manager that is responsible for the proxy.
     *
     * @return the proxy manager
     */
    ProxyManager<T> manager();

    /**
     * Intercepts the given method invocation. The {@link MethodInvokable} instance can be used to invoke the
     * method on the proxy, or to invoke the method on the super class. The {@link Invokable} instance can be
     * used to invoke the method on the proxy. The {@code self} and {@code args} parameters are the same as
     * those that would be passed to the method invocation. Depending on the implementation these parameters
     * may be ignored or processed before being passed to the method invocation.
     *
     * @param self the proxy instance
     * @param source the method that is being invoked
     * @param proxy the method that can be used to invoke the method on the proxy
     * @param args the arguments that are passed to the method invocation
     * @return the result of the method invocation
     * @throws Throwable if the method invocation fails
     */
    Object intercept(Object self, MethodInvokable source, Invokable proxy, Object[] args) throws Throwable;

}
