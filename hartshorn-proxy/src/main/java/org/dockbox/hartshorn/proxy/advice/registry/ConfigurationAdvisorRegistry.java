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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.advice.stub.DefaultValueResponseMethodStub;
import org.dockbox.hartshorn.proxy.advice.stub.MethodStub;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ConfigurationAdvisorRegistry<T> implements StateAwareAdvisorRegistry<T> {

    private final Map<Method, StateAwareMethodAdvisorRegistryStep<T, ?>> methodAdvisors = new ConcurrentHashMap<>();
    private final Map<Class<?>, StateAwareTypeAdvisorRegistryStep<?, T>> typeAdvisors = new ConcurrentHashMap<>();

    private final AdvisorRegistryState state = new SimpleAdvisorRegistryState();
    private final ApplicationProxier proxier;
    private final ProxyFactory<T> proxyFactory;

    private Supplier<MethodStub<T>> defaultStub = DefaultValueResponseMethodStub::new;

    public ConfigurationAdvisorRegistry(final ApplicationProxier proxier, final ProxyFactory<T> proxyFactory) {
        this.proxier = proxier;
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Class<T> advisedType() {
        return this.proxyFactory.type();
    }

    @Override
    public <R> StateAwareMethodAdvisorRegistryStep<T, R> method(final MethodView<T, R> method) {
        final StateAwareMethodAdvisorRegistryStep<T, ?> advisorStep = method.method()
                .map(this::method)
                .orElseThrow(() -> new IllegalArgumentException("Method view does not contain a method"));

        return TypeUtils.adjustWildcards(advisorStep, StateAwareMethodAdvisorRegistryStep.class);
    }

    @Override
    public StateAwareMethodAdvisorRegistryStep<T, Object> method(final Method method) {
        final StateAwareMethodAdvisorRegistryStep<T, ?> registryStep = methodAdvisors.computeIfAbsent(method,
                m -> new ConfigurationStateAwareMethodAdvisorRegistryStep<>(this, method));

        return TypeUtils.adjustWildcards(registryStep, StateAwareMethodAdvisorRegistryStep.class);
    }

    @Override
    public <S> StateAwareTypeAdvisorRegistryStep<S, T> type(final TypeView<S> type) {
        return this.type(type.type());
    }

    @Override
    public <S> StateAwareTypeAdvisorRegistryStep<S, T> type(final Class<S> type) {
        final Introspector introspector = this.proxier.introspector();
        if (introspector.introspect(type).isParentOf(this.advisedType())) {
            final StateAwareTypeAdvisorRegistryStep<?, T> advisorStep = typeAdvisors.computeIfAbsent(type,
                    t -> new ConfigurationStateAwareTypeAdvisorRegistryStep<>(this, type));

            return TypeUtils.adjustWildcards(advisorStep, StateAwareTypeAdvisorRegistryStep.class);
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
    public ConfigurationAdvisorRegistry<T> defaultStub(final MethodStub<T> stub) {
        return this.defaultStub(() -> stub);
    }

    @Override
    public ConfigurationAdvisorRegistry<T> defaultStub(final Supplier<MethodStub<T>> stub) {
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
