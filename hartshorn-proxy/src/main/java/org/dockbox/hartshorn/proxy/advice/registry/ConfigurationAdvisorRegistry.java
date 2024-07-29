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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.proxy.advice.stub.DefaultValueResponseMethodStub;
import org.dockbox.hartshorn.proxy.advice.stub.MethodStub;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A registry that allows for the configuration of all aspects of an advised type. This includes the configuration of
 * method stubs, method interceptors and type interceptors. The registry is aware of its state, and can be used as both
 * a mutable {@link AdvisorRegistry} and an immutable {@link org.dockbox.hartshorn.proxy.advice.ProxyAdvisorResolver}.
 *
 * @param <T> the type of the advised object
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class ConfigurationAdvisorRegistry<T> implements StateAwareAdvisorRegistry<T> {

    private final Map<Method, StateAwareMethodAdvisorRegistryStep<T, ?>> methodAdvisors = new ConcurrentHashMap<>();
    private final Map<Class<?>, StateAwareTypeAdvisorRegistryStep<?, T>> typeAdvisors = new ConcurrentHashMap<>();

    private final AdvisorRegistryState state = new SimpleAdvisorRegistryState();
    private final ProxyOrchestrator proxyOrchestrator;
    private final ProxyFactory<T> proxyFactory;

    private Supplier<MethodStub<T>> defaultStub = DefaultValueResponseMethodStub::new;

    public ConfigurationAdvisorRegistry(ProxyOrchestrator proxyOrchestrator, ProxyFactory<T> proxyFactory) {
        this.proxyOrchestrator = proxyOrchestrator;
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Class<T> advisedType() {
        return this.proxyFactory.type();
    }

    @Override
    public <R> StateAwareMethodAdvisorRegistryStep<T, R> method(MethodView<T, R> method) {
        StateAwareMethodAdvisorRegistryStep<T, ?> advisorStep = method.method()
                .map(this::method)
                .orElseThrow(() -> new IllegalArgumentException("Method view does not contain a method"));

        return TypeUtils.unchecked(advisorStep, StateAwareMethodAdvisorRegistryStep.class);
    }

    @Override
    public StateAwareMethodAdvisorRegistryStep<T, Object> method(Method method) {
        StateAwareMethodAdvisorRegistryStep<T, ?> registryStep = this.methodAdvisors.computeIfAbsent(method,
                method0 -> new ConfigurationStateAwareMethodAdvisorRegistryStep<>(this, method));

        return TypeUtils.unchecked(registryStep, StateAwareMethodAdvisorRegistryStep.class);
    }

    @Override
    public <S> StateAwareTypeAdvisorRegistryStep<S, T> type(TypeView<S> type) {
        return this.type(type.type());
    }

    @Override
    public <S> StateAwareTypeAdvisorRegistryStep<S, T> type(Class<S> type) {
        Introspector introspector = this.proxyOrchestrator.introspector();
        if (introspector.introspect(type).isParentOf(this.advisedType())) {
            StateAwareTypeAdvisorRegistryStep<?, T> advisorStep = this.typeAdvisors.computeIfAbsent(type,
                    type0 -> new ConfigurationStateAwareTypeAdvisorRegistryStep<>(this, type));

            return TypeUtils.unchecked(advisorStep, StateAwareTypeAdvisorRegistryStep.class);
        }
        else {
            throw new IllegalArgumentException(this.advisedType().getName() + " does not " + (type.isInterface() ? "implement " : "extend ") + type);
        }
    }

    @Override
    public StateAwareTypeAdvisorRegistryStep<T, T> type() {
        return this.type(this.advisedType());
    }

    @Override
    public ConfigurationAdvisorRegistry<T> defaultStub(MethodStub<T> stub) {
        return this.defaultStub(() -> stub);
    }

    @Override
    public ConfigurationAdvisorRegistry<T> defaultStub(Supplier<MethodStub<T>> stub) {
        this.defaultStub = Objects.requireNonNull(stub);
        return this;
    }

    @NonNull
    @Override
    public Supplier<MethodStub<T>> defaultStub() {
        return this.defaultStub;
    }

    @Override
    public AdvisorRegistryState state() {
        return this.state;
    }
}
