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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.proxy.advice.registry.AdvisorRegistry;
import org.dockbox.hartshorn.proxy.advice.registry.ConfigurationAdvisorRegistry;
import org.dockbox.hartshorn.proxy.advice.registry.StateAwareAdvisorRegistry;
import org.dockbox.hartshorn.proxy.constraint.CollectorProxyValidator;
import org.dockbox.hartshorn.proxy.constraint.ProxyConstraintViolation;
import org.dockbox.hartshorn.proxy.constraint.ProxyConstraintViolationException;
import org.dockbox.hartshorn.proxy.constraint.ProxyValidator;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * The default implementation of {@link ProxyFactory}. This implementation is state-aware, as is suggested by its
 * implementation of {@link StateAwareProxyFactory}. This means the factory can stop and start tracking state,
 * which is useful for testing. This implementation keeps track of known delegates and interceptors, allowing them
 * to be passed to the {@link ProxyManager} to manage proxies.
 *
 * <p>This implementation is unaware of any specific {@link ProxyManager} implementation, and therefore does not
 * know how to create proxies. This is the responsibility of the implementing class.
 *
 * @param <T> The parent type of the proxy.
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public abstract class DefaultProxyFactory<T> implements StateAwareProxyFactory<T>, ValidatorProxyFactory<T> {

    /**
     * The {@link NameGenerator} used to generate names for the proxy classes. This is used to ensure that the
     * generated proxy classes are unique. This field may be replaced at any time, and the factory will not be
     * affected.
     */
    protected static NameGenerator nameGenerator = new NameGenerator() {
        private final String sep = "_$$_hh" + Integer.toHexString(this.hashCode() & 0xfff) + "_";
        private int counter;

        @Override
        public String get(Class<?> type) {
            return this.get(type.getName());
        }

        @Override
        public String get(String type) {
            return type + this.sep + Integer.toHexString(this.counter++);
        }
    };

    private final Set<Class<?>> interfaces = ConcurrentHashMap.newKeySet();
    private final ProxyContextContainer contextContainer;
    private final ProxyOrchestrator proxyOrchestrator;
    private final StateAwareAdvisorRegistry<T> advisorRegistry;
    private final ProxyValidator validator;
    private final Class<T> type;

    protected DefaultProxyFactory(Class<T> type, ProxyOrchestrator proxyOrchestrator) {
        this.type = type;
        this.proxyOrchestrator = proxyOrchestrator;
        this.advisorRegistry = new ConfigurationAdvisorRegistry<>(proxyOrchestrator, this);
        this.contextContainer = new ProxyContextContainer(() -> this.advisorRegistry.state().modify());
        this.validator = new CollectorProxyValidator().withDefaults();
    }

    @Override
    public StateAwareAdvisorRegistry<T> advisors() {
        return this.advisorRegistry;
    }

    @Override
    public StateAwareProxyFactory<T> advisors(Consumer<? super AdvisorRegistry<T>> registryConsumer) {
        registryConsumer.accept(this.advisorRegistry);
        return this;
    }

    @Override
    public DefaultProxyFactory<T> implement(Class<?>... interfaces) {
        for (Class<?> anInterface : interfaces) {
            if (!anInterface.isInterface()) {
                throw new IllegalArgumentException(anInterface.getName() + " is not an interface");
            }
            if (Proxy.class.equals(anInterface)) {
                continue;
            }
            this.interfaces.add(anInterface);
        }
        return this;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public StateAwareProxyFactory<T> trackState(boolean trackState) {
        this.advisorRegistry.state().trackState(trackState);
        return this;
    }

    @Override
    public boolean modified() {
        return this.advisorRegistry.state().modified();
    }

    @Override
    public Set<Class<?>> interfaces() {
        return this.interfaces;
    }

    @Override
    public ProxyContextContainer contextContainer() {
        return this.contextContainer;
    }

    @Override
    public Option<T> proxy() throws ApplicationException {
        this.validateConstraints();
        return this.createNewProxy();
    }

    protected abstract Option<T> createNewProxy() throws ApplicationException;

    @Override
    public Option<T> proxy(Constructor<? extends T> constructor, Object[] args) throws ApplicationException {
        this.validateConstraints();
        return this.createNewProxy(constructor, args);
    }

    protected abstract Option<T> createNewProxy(Constructor<? extends T> constructor, Object[] args) throws ApplicationException;

    @Override
    public Option<T> proxy(ConstructorView<? extends T> constructor, Object[] args) throws ApplicationException {
        if (constructor.constructor().present()) {
            return this.proxy(constructor.constructor().get(), args);
        }
        else {
            throw new ApplicationException("Constructor " + constructor + " is not present on type " + this.type());
        }
    }

    protected void validateConstraints() throws ProxyConstraintViolationException {
        TypeView<T> typeView = this.orchestrator().introspector().introspect(this.type);
        Set<ProxyConstraintViolation> violations = this.validator().validate(typeView);

        if (!violations.isEmpty()) {
            throw new ProxyConstraintViolationException(violations);
        }
    }

    @Override
    public ProxyValidator validator() {
        return this.validator;
    }

    /**
     * Returns the {@link ProxyOrchestrator} that owns this factory.
     *
     * @return the {@link ProxyOrchestrator} that owns this factory
     */
    public ProxyOrchestrator orchestrator() {
        return this.proxyOrchestrator;
    }
}
