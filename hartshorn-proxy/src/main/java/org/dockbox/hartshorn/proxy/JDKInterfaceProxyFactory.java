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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyAdvisorMethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.function.CheckedFunction;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link ProxyFactory} implementation which uses the JDK {@link java.lang.reflect.Proxy} class to create proxies for
 * interfaces. This implementation is not capable of creating proxies for concrete or abstract classes, as the JDK
 * {@link java.lang.reflect.Proxy} class does not support this. Implementations of this class are expected to provide
 * their own implementation for creating proxies for concrete or abstract classes.
 *
 * @param <T> The type of the proxy to create
 *
 * @since 0.4.12
 * @author Guus Lieben
 */
public abstract class JDKInterfaceProxyFactory<T> extends DefaultProxyFactory<T> {

    protected JDKInterfaceProxyFactory(Class<T> type, ProxyOrchestrator proxyOrchestrator) {
        super(type, proxyOrchestrator);
    }

    @Override
    public Option<T> createNewProxy() throws ApplicationException {
        return this.createProxy(interceptor -> this.type().isInterface()
                        ? this.interfaceProxy(interceptor)
                        : this.concreteOrAbstractProxy(interceptor));
    }

    @Override
    public Option<T> createNewProxy(Constructor<? extends T> constructor, Object[] args) throws ApplicationException {
        if (args.length != constructor.getParameterCount()) {
            throw new ProxyConstructionException("Invalid number of arguments for constructor " + constructor);
        }
        if (this.type().isInterface()) {
            return this.proxy(); // Cannot invoke constructor on interface
        }
        else {
            return this.createProxy(interceptor -> this.concreteOrAbstractProxy(interceptor, constructor, args));
        }
    }

    /**
     * Creates a proxy for the given type. This prepares the {@link ProxyManager} and {@link ProxyMethodInterceptor},
     * ensuring any configured context is transferred to the proxy.
     *
     * @param instantiate The function to create the proxy
     * @return The proxy
     * @throws ApplicationException When the proxy cannot be created
     */
    protected Option<T> createProxy(CheckedFunction<ProxyMethodInterceptor<T>, Option<T>> instantiate) throws ApplicationException {
        LazyProxyManager<T> manager = new LazyProxyManager<>(this);

        this.contextContainer().contexts().forEach(manager::addContext);
        this.contextContainer().namedContexts().forEach(manager::addContext);

        ProxyMethodInterceptor<T> interceptor = new ProxyAdvisorMethodInterceptor<>(manager, this.orchestrator());

        Option<T> proxy = instantiate.apply(interceptor);

        proxy.peek(manager::proxy);
        return proxy;
    }

    /**
     * Creates an invocation handler for the given interceptor. This is used to intercept method calls on the proxy if
     * the proxy is an interface.
     *
     * @param interceptor The interceptor to use
     * @return The invocation handler
     */
    protected InvocationHandler invocationHandler(ProxyMethodInterceptor<T> interceptor) {
        return (self, method, args) -> interceptor.intercept(self, new MethodInvokable(method, this.orchestrator().introspector()), null, args);
    }

    /**
     * Creates a {@link ProxyConstructorFunction} for the given interceptor. This is used to create the proxy if the
     * proxy is a concrete or abstract class.
     *
     * @param interceptor The interceptor to use
     * @return The constructor function
     */
    protected abstract ProxyConstructorFunction<T> concreteOrAbstractEnhancer(ProxyMethodInterceptor<T> interceptor);

    /**
     * Creates a proxy if the type is a concrete or abstract class. This will attempt to use the default constructor
     * to create the proxy. If the default constructor is not available, an exception will be thrown.
     *
     * @param interceptor The interceptor to use
     * @return The proxy
     * @throws ApplicationException When the proxy cannot be created
     */
    protected Option<T> concreteOrAbstractProxy(ProxyMethodInterceptor<T> interceptor) throws ApplicationException {
        return this.createClassProxy(interceptor, ProxyConstructorFunction::create);
    }

    /**
     * Creates a proxy if the type is a concrete or abstract class. This will attempt to use the given constructor to
     * create the proxy. If the constructor is not available, an exception will be thrown.
     *
     * @param interceptor The interceptor to use
     * @param constructor The constructor to use
     * @param args The arguments to pass to the constructor
     * @return The proxy
     * @throws ApplicationException When the proxy cannot be created
     */
    protected Option<T> concreteOrAbstractProxy(ProxyMethodInterceptor<T> interceptor, Constructor<? extends T> constructor, Object[] args) throws ApplicationException {
        return this.createClassProxy(interceptor, enhancer -> enhancer.create(constructor, args));
    }

    /**
     * Creates a proxy if the type is a concrete or abstract class. This will attempt to use the given construction
     * function to create the proxy. If the construction function fails, an exception will be thrown.
     *
     * @param interceptor The interceptor to use
     * @param instantiate The construction function to use
     * @return The proxy
     * @throws ApplicationException When the proxy cannot be created
     */
    protected Option<T> createClassProxy(ProxyMethodInterceptor<T> interceptor, CheckedFunction<ProxyConstructorFunction<T>, T> instantiate) throws ApplicationException {
        ProxyConstructorFunction<T> enhancer = this.concreteOrAbstractEnhancer(interceptor);
        try {
            T proxy = instantiate.apply(enhancer);
            Option<T> delegate = this.advisors().type().delegate();
            if (delegate.present()) {
                this.restoreFields(delegate.get(), proxy);
            }
            return Option.of(proxy);
        }
        catch (Throwable e) {
            throw new ProxyConstructionException(e);
        }
    }

    /**
     * Gets the interfaces which should be implemented by the proxy class. This will always include the {@link Proxy}
     * interface. If the target type is an interface, it will also be included. Any additional interfaces configured
     * through {@link #implement(Class[])} will be included as well.
     *
     * @param includeType Whether or not to include the target type
     * @return The interfaces to implement
     */
    protected Class<?>[] proxyInterfaces(boolean includeType) {
        Class<?>[] standardInterfaces = includeType
                ? new Class<?>[] { Proxy.class, this.type() }
                : new Class<?>[] { Proxy.class };
        return CollectionUtilities.merge(standardInterfaces, this.interfaces().toArray(new Class[0]));
    }

    /**
     * Creates a proxy if the type is an interface. This will use the {@link java.lang.reflect.Proxy} class to create
     * the proxy.
     *
     * @param interceptor The interceptor to use
     * @return The proxy
     */
    protected Option<T> interfaceProxy(ProxyMethodInterceptor<T> interceptor) {
        Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                this.defaultClassLoader(),
                this.proxyInterfaces(true),
                this.invocationHandler(interceptor));
        return Option.of(this.type().cast(proxy));
    }

    /**
     * If possible, restores the fields of the delegate to the values of the proxy. This is only possible if the
     * delegate is available.
     *
     * @param existing The existing delegate
     * @param proxy The proxy
     */
    protected void restoreFields(T existing, T proxy) throws Throwable {
        TypeView<T> typeView = this.advisors().type().delegate()
                .map(this.orchestrator().introspector()::introspect)
                .orElseGet(() -> this.orchestrator().introspector().introspect(this.type()));

        for (FieldView<T, ?> field : typeView.fields().all()) {
            if (field.modifiers().isStatic()) {
                continue;
            }
            field.set(proxy, field.get(existing).orNull());
        }
    }

    /**
     * Gets the default class loader to use when creating a proxy. This will attempt to use the nearest available
     * class loader in the following order:
     * <ol>
     *     <li>The current thread's context class loader</li>
     *     <li>The class loader of this factory</li>
     *     <li>The system class loader</li>
     *     <li>The class loader of the target type</li>
     * </ol>
     *
     * @return The default class loader
     */
    protected ClassLoader defaultClassLoader() {
        return Option.of(Thread.currentThread()::getContextClassLoader)
                .orCompute(JDKInterfaceProxyFactory.class::getClassLoader)
                .orCompute(ClassLoader::getSystemClassLoader)
                .orCompute(this.type()::getClassLoader)
                .orNull();
    }
}
