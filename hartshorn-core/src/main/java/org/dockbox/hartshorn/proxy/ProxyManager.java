/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.context.ApplicationAwareContext;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * A proxy manager is responsible for managing the lifecycle of a single proxy object. How the proxy is created is
 * determined by the {@link ProxyFactory} that is used to create the proxy. The proxy manager is responsible for
 * providing the proxy with the necessary information to function.
 *
 * <p>The necessary information is provided by the {@link ProxyFactory} and the {@link ProxyManager} is responsible
 * for providing it to the proxy after it has been created. As a legal side effect of this, the proxy manager is
 * capable of exposing the active delegates, interceptors, and proxy context.
 *
 * @param <T> the type of the proxy
 * @author Guus Lieben
 * @since 22.2
 */
public interface ProxyManager<T> extends ApplicationAwareContext {

    /**
     * Returns the original type of the proxy. This is the type of the object that is proxied, but is not the proxied
     * type itself.
     *
     * @return the original type of the proxy
     */
    Class<T> targetClass();

    /**
     * Gets the proxied type of the proxy. This is the type of the object that is proxied, but is not the original
     * type of the proxy.
     *
     * @return the proxied type of the proxy
     */
    Class<T> proxyClass();

    /**
     * Returns the proxy instance managed by this manager.
     *
     * @return the proxy instance managed by this manager
     */
    T proxy();

    /**
     * Returns the original instance delegate of the proxy.
     * @return the original instance delegate of the proxy
     * @see ProxyFactory#typeDelegate()
     */
    Option<T> delegate();

    /**
     * Returns the delegate for the given method. This method is used to obtain the delegate for the given method, which
     * may be either a subtype of the original type, or a supertype of the original type.
     *
     * @param method the method for which to obtain the delegate
     * @return the delegate for the given method
     */
    Option<T> delegate(Method method);

    /**
     * Returns the delegate for the given type. This method is used to obtain the delegate for the given type, which is
     * always a subtype of the original type.
     *
     * @param type the type for which to obtain the delegate
     * @param <S> the type of the delegate
     * @return the delegate for the given type
     */
    <S> Option<S> delegate(Class<S> type);

    /**
     * Gets the interceptor for the given method. This method is used to obtain the interceptor for the given method,
     * which may be a chained or single interceptor. If the method is not intercepted, this method returns {@link Option#empty()}
     *
     * @param method the method for which to obtain the interceptor
     * @return the interceptor for the given method
     */
    Option<MethodInterceptor<T, ?>> interceptor(Method method);

    /**
     * Gets all method wrappers for the given method. If the method is not intercepted, this method returns an empty set.
     *
     * @param method the method for which to obtain the method wrappers
     * @return all method wrappers for the given method
     */
    Set<MethodWrapper<T>> wrappers(Method method);

    /**
     * Gets the default method stub for the proxy. Stubs are used to provide a default implementation
     * for methods that are not otherwise delegated or intercepted.
     *
     * @return the default method stub for the proxy
     * @see ProxyFactory#defaultStub()
     * @see MethodStub
     * @see MethodStubContext
     */
    MethodStub<T> stub();
}
