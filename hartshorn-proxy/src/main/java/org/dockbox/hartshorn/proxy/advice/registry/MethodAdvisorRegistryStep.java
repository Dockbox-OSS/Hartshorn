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

package org.dockbox.hartshorn.proxy.advice.registry;

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapperFactory;
import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallbackContext;

import java.util.function.Consumer;

/**
 * Configuration step for {@link AdvisorRegistry}s. This step is used to configure the registry by adding
 * advisors for specific methods.
 *
 * @param <T> the type of the proxy object
 * @param <R> the return type of the method
 *
 * @see AdvisorRegistry
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface MethodAdvisorRegistryStep<T, R> {

    /**
     * Delegates the given method to the given delegate instance. This targets a backing implementation,
     * not the original instance.
     *
     * @param delegateInstance the instance to which the method is delegated
     * @return the registry, for chaining
     */
    AdvisorRegistry<T> delegate(T delegateInstance);

    /**
     * Intercepts the given method and replaces it with the given {@link MethodInterceptor}. If there is
     * already an interceptor for the given method, it will be chained, so it may be executed in series.
     *
     * @param interceptor the interceptor to execute
     * @return the registry, for chaining
     */
    AdvisorRegistry<T> intercept(MethodInterceptor<T, R> interceptor);

    /**
     * Intercepts the given method and calls the given {@link MethodWrapper} for all known phases of the
     * wrapper. These phases are:
     * <ul>
     *     <li>{@link MethodWrapper#acceptBefore(ProxyCallbackContext)}: Before entry</li>
     *     <li>{@link MethodWrapper#acceptAfter(ProxyCallbackContext)}: After return</li>
     *     <li>{@link MethodWrapper#acceptError(ProxyCallbackContext)}: After exception thrown</li>
     * </ul>
     *
     * @param wrapper the wrapper to execute
     * @return the registry, for chaining
     */
    AdvisorRegistry<T> wrapAround(MethodWrapper<T> wrapper);

    /**
     * Constructs a {@link MethodWrapper} using the given {@link MethodWrapperFactory} and calls the
     * resulting {@link MethodWrapper} for all known phases of the wrapper. These phases are:
     * <ul>
     *     <li>{@link MethodWrapper#acceptBefore(ProxyCallbackContext)}: Before entry</li>
     *     <li>{@link MethodWrapper#acceptAfter(ProxyCallbackContext)}: After return</li>
     *     <li>{@link MethodWrapper#acceptError(ProxyCallbackContext)}: After exception thrown</li>
     * </ul>
     *
     * @param wrapper the wrapper to execute
     * @return the registry, for chaining
     */
    AdvisorRegistry<T> wrapAround(Consumer<MethodWrapperFactory<T>> wrapper);

}
