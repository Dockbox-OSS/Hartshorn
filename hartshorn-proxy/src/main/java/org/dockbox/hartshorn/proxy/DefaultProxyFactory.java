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

package org.dockbox.hartshorn.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.ConcurrentClassMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

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
 * @author Guus Lieben
 * @since 22.2
 */
public abstract class DefaultProxyFactory<T> implements StateAwareProxyFactory<T> {

    /**
     * The {@link NameGenerator} used to generate names for the proxy classes. This is used to ensure that the
     * generated proxy classes are unique. This field may be replaced at any time, and the factory will not be
     * affected.
     */
    protected static NameGenerator nameGenerator = new NameGenerator() {
        private final String sep = "_$$_hh" + Integer.toHexString(this.hashCode() & 0xfff) + "_";
        private int counter;

        @Override
        public String get(final Class<?> type) {
            return this.get(type.getName());
        }

        @Override
        public String get(final String type) {
            return type + this.sep + Integer.toHexString(this.counter++);
        }
    };

    private static final String GROOVY_TRAIT = "groovy.transform.Trait";

    // Delegates and interceptors
    private final Map<Method, Object> delegates = new ConcurrentHashMap<>();
    private final Map<Method, MethodInterceptor<T, ?>> interceptors = new ConcurrentHashMap<>();
    private final MultiMap<Method, MethodWrapper<T>> wrappers = new ConcurrentSetMultiMap<>();
    private final ConcurrentClassMap<Object> typeDelegates = new ConcurrentClassMap<>();
    private final Set<Class<?>> interfaces = ConcurrentHashMap.newKeySet();
    private T typeDelegate;
    private Supplier<MethodStub<T>> defaultStub = DefaultValueResponseMethodStub::new;

    // Proxy data
    private final ProxyContextContainer contextContainer = new ProxyContextContainer(this::updateState);
    private final Class<T> type;
    private final ApplicationProxier applicationProxier;

    private boolean trackState = true;
    private boolean modified;

    protected DefaultProxyFactory(final Class<T> type, final ApplicationProxier applicationProxier) {
        this.type = type;
        this.applicationProxier = applicationProxier;
        this.validate();
    }

    protected void validate() {
        if (this.isGroovyTrait(this.type))
            throw new IllegalArgumentException("Cannot create proxy for Groovy trait " + this.type.getName());
    }

    protected boolean isGroovyTrait(final Class<?> type) {
        try {
            final Class<?> groovyTrait = Class.forName(GROOVY_TRAIT);
            return groovyTrait.isAnnotation() && type.isAnnotationPresent((Class<? extends Annotation>) groovyTrait);
        }
        catch (final ClassNotFoundException e) {
            return false;
        }
    }

    protected void updateState() {
        if (this.trackState) this.modified = true;
    }

    @Override
    public DefaultProxyFactory<T> delegate(final T delegate) {
        if (delegate != null) {
            this.updateState();
            for (final Method declaredMethod : this.type.getDeclaredMethods()) {
                this.delegates.put(declaredMethod, delegate);
            }
            this.typeDelegate = delegate;
        }
        return this;
    }

    @Override
    public DefaultProxyFactory<T> delegateAbstract(final T delegate) {
        if (delegate != null) {
            this.updateState();
            for (final Method declaredMethod : this.type.getDeclaredMethods()) {
                this.delegateAbstractOverrideCandidate(delegate, declaredMethod);
            }
            this.typeDelegate = delegate;
        }
        return this;
    }

    private <S> void delegateAbstractOverrideCandidate(final S delegate, final Method declaredMethod) {
        try {
            final Method override = this.type().getMethod(declaredMethod.getName(), declaredMethod.getParameterTypes());
            if (!Modifier.isAbstract(override.getModifiers()) || override.isDefault() || declaredMethod.isDefault()) {
                return;
            }
        } catch (final NoSuchMethodException e) {
            // Ignore error, delegate is not concrete
        }
        this.delegates.put(declaredMethod, delegate);
    }

    @Override
    public <S> DefaultProxyFactory<T> delegate(final Class<S> type, final S delegate) {
        if (type.isAssignableFrom(this.type)) {
            this.updateState();
            for (final Method declaredMethod : type.getDeclaredMethods()) {
                this.delegates.put(declaredMethod, delegate);
            }
            this.typeDelegates.put((Class<Object>) type, delegate);
        }
        else {
            throw new IllegalArgumentException(this.type.getName() + " does not " + (type.isInterface() ? "implement " : "extend ") + type);
        }
        return this;
    }

    @Override
    public <S> DefaultProxyFactory<T> delegateAbstract(final Class<S> type, final S delegate) {
        if (type.isAssignableFrom(this.type)) {
            this.updateState();
            for (final Method declaredMethod : type.getDeclaredMethods()) {
                this.delegateAbstractOverrideCandidate(delegate, declaredMethod);
            }
        }
        else {
            throw new IllegalArgumentException(this.type.getName() + " does not " + (type.isInterface() ? "implement " : "extend ") + type);
        }
        return this;
    }

    @Override
    public DefaultProxyFactory<T> delegate(final Method method, final T delegate) {
        this.updateState();
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null");
        }
        final TypeView<T> delegateType = this.applicationProxier().introspector().introspect(delegate);
        if (!delegateType.isChildOf(method.getDeclaringClass())) {
            throw new IllegalArgumentException("Delegate must implement- or be of type " + method.getDeclaringClass().getName());
        }
        this.delegates.put(method, delegate);
        return this;
    }

    @Override
    public DefaultProxyFactory<T> intercept(final Method method, final MethodInterceptor<T, ?> interceptor) {
        final MethodInterceptor<T, ?> methodInterceptor;
        if (this.interceptors.containsKey(method)) {
            methodInterceptor = this.interceptors.get(method)
                    .andThen(TypeUtils.adjustWildcards(interceptor, MethodInterceptor.class));
        }
        else {
            methodInterceptor = interceptor;
        }
        this.updateState();
        this.interceptors.put(method, methodInterceptor);
        return this;
    }

    @Override
    public DefaultProxyFactory<T> wrapAround(final Method method, final MethodWrapper<T> wrapper) {
        this.updateState();
        this.wrappers.put(method, wrapper);
        return this;
    }

    @Override
    public StateAwareProxyFactory<T> wrapAround(final Method method, final Consumer<MethodWrapperFactory<T>> wrapper) {
        final StandardMethodWrapperFactory<T> factory = new StandardMethodWrapperFactory<>(this);
        wrapper.accept(factory);
        return this.wrapAround(method, factory.create());
    }

    @Override
    public StateAwareProxyFactory<T> wrapAround(final MethodView<T, ?> method, final Consumer<MethodWrapperFactory<T>> wrapper) {
        return method.method().map(m -> this.wrapAround(m, wrapper)).orElse(this);
    }


    @Override
    public StateAwareProxyFactory<T> delegate(final MethodView<T, ?> method, final T delegate) {
        return method.method().map(m -> this.delegate(m, delegate)).orElse(this);
    }

    @Override
    public <R> StateAwareProxyFactory<T> intercept(final MethodView<T, R> method, final MethodInterceptor<T, R> interceptor) {
        return method.method().map(m -> this.intercept(m, interceptor)).orElse(this);
    }

    @Override
    public StateAwareProxyFactory<T> wrapAround(final MethodView<T, ?> method, final MethodWrapper<T> wrapper) {
        return method.method().map(m -> this.wrapAround(m, wrapper)).orElse(this);
    }

    @Override
    public DefaultProxyFactory<T> implement(final Class<?>... interfaces) {
        for (final Class<?> anInterface : interfaces) {
            if (!anInterface.isInterface()) {
                throw new IllegalArgumentException(anInterface.getName() + " is not an interface");
            }
            if (Proxy.class.equals(anInterface)) continue;
            this.interfaces.add(anInterface);
        }
        return this;
    }

    @Override
    public DefaultProxyFactory<T> defaultStub(final MethodStub<T> stub) {
        return this.defaultStub(() -> stub);
    }

    @Override
    public DefaultProxyFactory<T> defaultStub(final Supplier<MethodStub<T>> stub) {
        this.defaultStub = stub;
        return this;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public StateAwareProxyFactory<T> trackState(final boolean trackState) {
        this.trackState = trackState;
        return this;
    }

    @Override
    public boolean modified() {
        return this.modified;
    }

    @Override
    public T typeDelegate() {
        return this.typeDelegate;
    }

    @Override
    public Map<Method, Object> delegates() {
        return this.delegates;
    }

    @Override
    public Map<Method, MethodInterceptor<T, ?>> interceptors() {
        return this.interceptors;
    }

    @Override
    public ConcurrentClassMap<Object> typeDelegates() {
        return this.typeDelegates;
    }

    @Override
    public MultiMap<Method, MethodWrapper<T>> wrappers() {
        return this.wrappers;
    }

    @Override
    public Supplier<MethodStub<T>> defaultStub() {
        return this.defaultStub;
    }

    @Override
    public Set<Class<?>> interfaces() {
        return this.interfaces;
    }

    /**
     * Returns whether the factory is tracking its state.
     * @return {@code true} if the factory is tracking its state, {@code false} otherwise.
     */
    public boolean trackState() {
        return this.trackState;
    }

    @Override
    public ProxyContextContainer contextContainer() {
        return this.contextContainer;
    }

    public ApplicationProxier applicationProxier() {
        return this.applicationProxier;
    }
}
