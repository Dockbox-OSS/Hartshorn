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

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapperFactory;
import org.dockbox.hartshorn.proxy.advice.wrap.StandardMethodWrapperFactory;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Basic implementation of {@link StateAwareMethodAdvisorRegistryStep}. This implementation is used to configure the
 * {@link StateAwareAdvisorRegistry} by adding {@link MethodInterceptor}s and {@link MethodWrapper}s. As both the
 * registry and this registry step are stateful, the registry is marked as modified when this step is configured.
 *
 * @param <T> the type of the proxy object
 * @param <R> the return type of the method
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class ConfigurationStateAwareMethodAdvisorRegistryStep<T, R> implements StateAwareMethodAdvisorRegistryStep<T, R> {

    private final Collection<MethodWrapper<T>> wrappers = ConcurrentHashMap.newKeySet();
    private final StateAwareAdvisorRegistry<T> registry;
    private final Method method;

    private MethodInterceptor<T, R> interceptor;
    private T delegate;

    public ConfigurationStateAwareMethodAdvisorRegistryStep(StateAwareAdvisorRegistry<T> registry, Method method) {
        this.registry = registry;
        this.method = method;
        this.verifyConstraints();
    }

    private void verifyConstraints() {
        if (Modifier.isFinal(this.method.getModifiers())) {
            throw new IllegalArgumentException("Cannot create advisor for method " + this.method.getName());
        }
    }

    @Override
    public AdvisorRegistry<T> delegate(T delegateInstance) {
        if (delegateInstance == null) {
            throw new IllegalArgumentException("Delegate cannot be null");
        }
        this.delegate = delegateInstance;
        return this.exit();
    }

    @Override
    public AdvisorRegistry<T> intercept(MethodInterceptor<T, R> interceptor) {
        if (this.interceptor != null) {
            this.interceptor = this.interceptor.andThen(interceptor);
        }
        else {
            this.interceptor = interceptor;
        }
        return this.exit();
    }

    @Override
    public AdvisorRegistry<T> wrapAround(MethodWrapper<T> wrapper) {
        this.wrappers.add(wrapper);
        return this.exit();
    }

    private StateAwareAdvisorRegistry<T> exit() {
        this.registry.state().modify();
        return this.registry;
    }

    @Override
    public AdvisorRegistry<T> wrapAround(Consumer<MethodWrapperFactory<T>> wrapper) {
        StandardMethodWrapperFactory<T> factory = new StandardMethodWrapperFactory<>();
        wrapper.accept(factory);
        return this.wrapAround(factory.create());
    }

    @Override
    public Option<T> delegate() {
        return Option.of(this.delegate);
    }

    @Override
    public Option<MethodInterceptor<T, R>> interceptor() {
        return Option.of(this.interceptor);
    }

    @Override
    public Collection<MethodWrapper<T>> wrappers() {
        return this.wrappers;
    }
}
