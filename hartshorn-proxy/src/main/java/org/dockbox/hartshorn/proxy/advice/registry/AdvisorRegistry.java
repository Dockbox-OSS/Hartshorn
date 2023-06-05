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

package org.dockbox.hartshorn.proxy.advice.registry;

import org.dockbox.hartshorn.proxy.advice.stub.MethodStub;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * A mutable registry to add advisors to a proxy instance. Advisors can be added for specific methods or for all methods
 * of a specific type. The registry can be used to add a default stub for all methods. Valid advisors are:
 * <ul>
 *     <li>{@link org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper}</li>
 *     <li>{@link org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor}</li>
 *     <li>Delegate instances of {@link T}</li>
 *     <li>{@link MethodStub}</li>
 * </ul>
 *
 * <p>Note that the registry should only be mutable during the creation of a proxy instance. After the proxy instance
 * has been created, the configured advisors are transferred to the {@link org.dockbox.hartshorn.proxy.ProxyManager}
 * where they may be resolved through a {@link org.dockbox.hartshorn.proxy.advice.ProxyAdvisorResolver}.
 *
 * @param <T> the type of the proxy instance
 *
 * @since 23.1
 * @author Guus Lieben
 */
public interface AdvisorRegistry<T> {

    /**
     * Returns the type of the proxy which is being configured.
     * @return the type of the proxy which is being configured
     */
    Class<T> advisedType();

    /**
     * Gets or creates an advisor registry step for the given method. The method does not have to be a method of the
     * advised type, but it must be a method of a type which is assignable to the advised type.
     *
     * @param method the method to get or create a registry step for
     * @return the registry step for the given method
     * @param <R> the return type of the method
     */
    <R> MethodAdvisorRegistryStep<T, R> method(MethodView<T, R> method);

    /**
     * Gets or creates an advisor registry step for the given method. The method does not have to be a method of the
     * advised type, but it must be a method of a type which is assignable to the advised type.
     *
     * @param method the method to get or create a registry step for
     * @return the registry step for the given method
     */
    MethodAdvisorRegistryStep<T, Object> method(Method method);

    /**
     * Gets or creates an advisor registry step for the given type. The type must be assignable to the advised type.
     *
     * @param type the type to get or create a registry step for
     * @return the registry step for the given type
     * @param <S> the type of the registry step
     */
    <S> TypeAdvisorRegistryStep<S, T> type(Class<S> type);

    /**
     * Gets or creates an advisor registry step for the given type. The type must be assignable to the advised type.
     *
     * @param type the type to get or create a registry step for
     * @return the registry step for the given type
     * @param <S> the type of the registry step
     */
    <S> TypeAdvisorRegistryStep<S, T> type(TypeView<S> type);

    /**
     * Gets or creates an advisor registry step for the advised type.
     *
     * @return the registry step for the advised type
     */
    TypeAdvisorRegistryStep<T, T> type();

    /**
     * Sets the default stub for all methods. The stub will be used if no other advisor is configured for a method.
     *
     * @param stub the default stub
     * @return this registry
     */
    AdvisorRegistry<T> defaultStub(MethodStub<T> stub);

    /**
     * Sets the lazy-loaded default stub for all methods. The stub will be used if no other advisor is configured for a
     * method.
     *
     * @param stub the default stub
     * @return this registry
     */
    AdvisorRegistry<T> defaultStub(Supplier<MethodStub<T>> stub);

}
