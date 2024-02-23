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

import org.dockbox.hartshorn.proxy.advice.ProxyAdvisorResolver;
import org.dockbox.hartshorn.proxy.advice.stub.MethodStub;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * A registry that is aware of its state. This means that it can be queried for whether it has been modified since its
 * initial creation. This is used to determine whether the proxy should be created. If the registry was never modified
 * then the proxy is not required, and the original instance can be returned by the caller of the
 * {@link org.dockbox.hartshorn.proxy.ProxyFactory}.
 *
 * @param <T> the type of the target object
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public interface StateAwareAdvisorRegistry<T> extends AdvisorRegistry<T>, ProxyAdvisorResolver<T> {

    @Override
    <R> StateAwareMethodAdvisorRegistryStep<T, R> method(MethodView<T, R> method);

    @Override
    StateAwareMethodAdvisorRegistryStep<T, Object> method(Method method);

    @Override
    <S> StateAwareTypeAdvisorRegistryStep<S, T> type(Class<S> type);

    @Override
    <S> StateAwareTypeAdvisorRegistryStep<S, T> type(TypeView<S> type);

    @Override
    StateAwareTypeAdvisorRegistryStep<T, T> type();

    @Override
    StateAwareAdvisorRegistry<T> defaultStub(MethodStub<T> stub);

    @Override
    StateAwareAdvisorRegistry<T> defaultStub(Supplier<MethodStub<T>> stub);

    /**
     * Returns the state of the registry.
     *
     * @return the state of the registry
     */
    AdvisorRegistryState state();
}
