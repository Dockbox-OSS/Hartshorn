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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.function.CheckedFunction;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

public abstract class JDKInterfaceProxyFactory<T> extends DefaultProxyFactory<T> {

    protected JDKInterfaceProxyFactory(final Class<T> type, final ApplicationContext applicationContext) {
        super(type, applicationContext);
    }

    @Override
    public Attempt<T, Throwable> proxy() throws ApplicationException {
        return this.createProxy(interceptor -> this.type().isInterface()
                        ? this.interfaceProxy(interceptor)
                        : this.concreteOrAbstractProxy(interceptor));
    }

    @Override
    public Attempt<T, Throwable> proxy(final Constructor<T> constructor, final Object[] args) throws ApplicationException {
        if (args.length != constructor.getParameterCount()) {
            throw new ApplicationException("Invalid number of arguments for constructor " + constructor);
        }
        if (this.type().isInterface()) return this.proxy(); // Cannot invoke constructor on interface
        else return this.createProxy(interceptor -> this.concreteOrAbstractProxy(interceptor, constructor, args));
    }

    protected Attempt<T, Throwable> createProxy(final CheckedFunction<ProxyMethodInterceptor<T>, Attempt<T, Throwable>> instantiate) throws ApplicationException {
        final LazyProxyManager<T> manager = new LazyProxyManager<>(this);

        this.contextContainer().contexts().forEach(manager::add);
        this.contextContainer().namedContexts().forEach(manager::add);

        final ProxyMethodInterceptor<T> interceptor = new StandardMethodInterceptor<>(manager, this.applicationContext());

        final Attempt<T, Throwable> proxy = instantiate.apply(interceptor);

        proxy.peek(manager::proxy);
        return proxy;
    }

    protected InvocationHandler invocationHandler(final ProxyMethodInterceptor<T> interceptor) {
        return (self, method, args) -> interceptor.intercept(self, new MethodInvokable(method, this.applicationContext()), null, args);
    }

    protected abstract ProxyConstructorFunction<T> concreteOrAbstractEnhancer(ProxyMethodInterceptor<T> interceptor);

    protected Attempt<T, Throwable> concreteOrAbstractProxy(final ProxyMethodInterceptor<T> interceptor) throws ApplicationException {
        return this.createClassProxy(interceptor, ProxyConstructorFunction::create);
    }

    protected Attempt<T, Throwable> concreteOrAbstractProxy(final ProxyMethodInterceptor<T> interceptor, final Constructor<T> constructor, final Object[] args) throws ApplicationException {
        return this.createClassProxy(interceptor, enhancer -> enhancer.create(constructor, args));
    }

    protected Attempt<T, Throwable> createClassProxy(final ProxyMethodInterceptor<T> interceptor, final CheckedFunction<ProxyConstructorFunction<T>, T> instantiate) throws ApplicationException {
        final ProxyConstructorFunction<T> enhancer = this.concreteOrAbstractEnhancer(interceptor);
        try {
            final T proxy = instantiate.apply(enhancer);
            if (this.typeDelegate() != null) this.restoreFields(this.typeDelegate(), proxy);
            return Attempt.of(proxy);
        }
        catch (final RuntimeException e) {
            throw new ApplicationException(e);
        }
    }

    protected Class<?>[] proxyInterfaces(final boolean includeType) {
        final Class<?>[] standardInterfaces = includeType
                ? new Class<?>[] { Proxy.class, this.type() }
                : new Class<?>[] { Proxy.class };
        return CollectionUtilities.merge(standardInterfaces, this.interfaces().toArray(new Class[0]));
    }

    protected Attempt<T, Throwable> interfaceProxy(final ProxyMethodInterceptor<T> interceptor) {
        final Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                this.defaultClassLoader(),
                this.proxyInterfaces(true),
                this.invocationHandler(interceptor));
        return Attempt.of(this.type().cast(proxy));
    }

    protected void restoreFields(final T existing, final T proxy) {
        final TypeView<T> typeView = this.applicationContext().environment().isProxy(existing)
                ? this.applicationContext().environment().introspect(this.type())
                : this.applicationContext().environment().introspect(this.typeDelegate());

        for (final FieldView<T, ?> field : typeView.fields().all()) {
            if (field.isStatic()) continue;
            field.set(proxy, field.get(existing).orNull());
        }
    }

    protected ClassLoader defaultClassLoader() {
        return Option.of(Thread.currentThread()::getContextClassLoader)
                .orCompute(JDKInterfaceProxyFactory.class::getClassLoader)
                .orCompute(ClassLoader::getSystemClassLoader)
                .orCompute(this.type()::getClassLoader)
                .orNull();
    }
}
