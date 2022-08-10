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

import org.dockbox.hartshorn.util.CustomMultiMap;
import org.dockbox.hartshorn.util.MultiMap;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.TypeMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
public abstract class DefaultProxyFactory<T> implements StateAwareProxyFactory<T, DefaultProxyFactory<T>>, ContextCarrier {

    /**
     * The {@link NameGenerator} used to generate names for the proxy classes. This is used to ensure that the
     * generated proxy classes are unique. This field may be replaced at any time, and the factory will not be
     * affected.
     */
    public static NameGenerator NAME_GENERATOR = new NameGenerator() {
        private final String sep = "_$$_hh" + Integer.toHexString(this.hashCode() & 0xfff) + "_";
        private int counter = 0;

        @Override
        public String get(final TypeContext<?> type) {
            return this.get(type.type());
        }

        @Override
        public String get(final Class<?> type) {
            return this.get(type.getName());
        }

        @Override
        public String get(final String type) {
            return type + this.sep + Integer.toHexString(this.counter++);
        }
    };

    // Delegates and interceptors
    private final Map<Method, Object> delegates = new ConcurrentHashMap<>();
    private final Map<Method, MethodInterceptor<T>> interceptors = new ConcurrentHashMap<>();
    private final MultiMap<Method, MethodWrapper<T>> wrappers = new CustomMultiMap<>(ConcurrentHashMap::newKeySet);
    private final TypeMap<Object> typeDelegates = new TypeMap<>();
    private final Set<Class<?>> interfaces = ConcurrentHashMap.newKeySet();
    private T typeDelegate;

    // Proxy data
    private final ProxyContextContainer contextContainer = new ProxyContextContainer();
    private final Class<T> type;
    private final ApplicationContext applicationContext;

    private boolean trackState = true;
    private boolean modified;

    public DefaultProxyFactory(final Class<T> type, final ApplicationContext applicationContext) {
        this.type = type;
        this.applicationContext = applicationContext;
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
                try {
                    final Method override = this.type().getMethod(declaredMethod.getName(), declaredMethod.getParameterTypes());
                    if (!Modifier.isAbstract(override.getModifiers()) || override.isDefault() || declaredMethod.isDefault()) {
                        continue;
                    }
                } catch (final NoSuchMethodException e) {
                    // Ignore error, delegate is not concrete
                }
                this.delegates.put(declaredMethod, delegate);
            }
            this.typeDelegate = delegate;
        }
        return this;
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
                try {
                    final Method override = this.type().getMethod(declaredMethod.getName(), declaredMethod.getParameterTypes());
                    if (!Modifier.isAbstract(override.getModifiers()) || override.isDefault() || declaredMethod.isDefault()) {
                        continue;
                    }
                } catch (final NoSuchMethodException e) {
                    // Ignore error, delegate is not concrete
                }
                this.delegates.put(declaredMethod, delegate);
            }
        }
        else {
            throw new IllegalArgumentException(this.type.getName() + " does not " + (type.isInterface() ? "implement " : "extend ") + type);
        }
        return this;
    }

    @Override
    public DefaultProxyFactory<T> delegate(final MethodContext<?, T> method, final T delegate) {
        return this.delegate(method.method(), delegate);
    }

    @Override
    public DefaultProxyFactory<T> delegate(final Method method, final T delegate) {
        this.updateState();
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null");
        }
        if (!TypeContext.of(delegate).childOf(method.getDeclaringClass())) {
            throw new IllegalArgumentException("Delegate must implement- or be of type " + method.getDeclaringClass().getName());
        }
        this.delegates.put(method, delegate);
        return this;
    }

    @Override
    public DefaultProxyFactory<T> intercept(final MethodContext<?, T> method, final MethodInterceptor<T> interceptor) {
        return this.intercept(method.method(), interceptor);
    }

    @Override
    public DefaultProxyFactory<T> intercept(final Method method, final MethodInterceptor<T> interceptor) {
        final MethodInterceptor<T> methodInterceptor;
        if (this.interceptors.containsKey(method)) {
            methodInterceptor = this.interceptors.get(method).andThen(interceptor);
        }
        else {
            methodInterceptor = interceptor;
        }
        this.updateState();
        this.interceptors.put(method, methodInterceptor);
        return this;
    }

    @Override
    public DefaultProxyFactory<T> intercept(final MethodContext<?, T> method, final MethodWrapper<T> wrapper) {
        return this.intercept(method.method(), wrapper);
    }

    @Override
    public DefaultProxyFactory<T> intercept(final Method method, final MethodWrapper<T> wrapper) {
        this.updateState();
        this.wrappers.put(method, wrapper);
        return this;
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
    public Class<T> type() {
        return this.type;
    }

    @Override
    public StateAwareProxyFactory<T, DefaultProxyFactory<T>> trackState(final boolean trackState) {
        this.trackState = trackState;
        return this;
    }

    @Override
    public boolean modified() {
        return this.modified;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
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
    public Map<Method, MethodInterceptor<T>> interceptors() {
        return this.interceptors;
    }

    @Override
    public TypeMap<Object> typeDelegates() {
        return this.typeDelegates;
    }

    @Override
    public MultiMap<Method, MethodWrapper<T>> wrappers() {
        return this.wrappers;
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
}
