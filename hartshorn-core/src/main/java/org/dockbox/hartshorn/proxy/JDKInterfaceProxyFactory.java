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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.function.CheckedFunction;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.InvocationHandler;

public abstract class JDKInterfaceProxyFactory<T> extends DefaultProxyFactory<T> {

    protected JDKInterfaceProxyFactory(final Class<T> type, final ApplicationContext applicationContext) {
        super(type, applicationContext);
    }

    @Override
    public Result<T> proxy() throws ApplicationException {
        return this.createProxy(interceptor -> this.type().isInterface()
                ? this.interfaceProxy(interceptor)
                : this.concreteOrAbstractProxy(interceptor));
    }

    @Override
    public Result<T> proxy(final ConstructorView<T> constructor, final Object[] args) throws ApplicationException {
        if (args.length != constructor.parameters().count()) {
            throw new ApplicationException("Invalid number of arguments for constructor " + constructor);
        }
        if (this.type().isInterface()) return this.proxy(); // Cannot invoke constructor on interface
        else return this.createProxy(interceptor -> this.concreteOrAbstractProxy(interceptor, constructor, args));
    }

    protected Result<T> createProxy(final CheckedFunction<StandardMethodInterceptor<T>, Result<T>> instantiate) throws ApplicationException {
        final LazyProxyManager<T> manager = new LazyProxyManager<>(this.applicationContext(), this);

        this.contextContainer().contexts().forEach(manager::add);
        this.contextContainer().namedContexts().forEach(manager::add);

        final StandardMethodInterceptor<T> interceptor = new StandardMethodInterceptor<>(manager, this.applicationContext());

        final Result<T> proxy = instantiate.apply(interceptor);

        proxy.present(manager::proxy);
        return proxy;
    }

    protected InvocationHandler invocationHandler(final StandardMethodInterceptor<T> interceptor) {
        return (self, method, args) -> interceptor.intercept(self, new MethodInvokable(method, this.applicationContext()), null, args);
    }

    protected abstract ProxyConstructorFunction<T> concreteOrAbstractEnhancer(StandardMethodInterceptor<T> interceptor);

    protected Result<T> concreteOrAbstractProxy(final StandardMethodInterceptor<T> interceptor) throws ApplicationException {
        return this.createClassProxy(interceptor, ProxyConstructorFunction::create);
    }

    protected Result<T> concreteOrAbstractProxy(final StandardMethodInterceptor<T> interceptor, final ConstructorView<T> constructor, final Object[] args) throws ApplicationException {
        return this.createClassProxy(interceptor, enhancer -> enhancer.create(constructor, args));
    }

    protected Result<T> createClassProxy(final StandardMethodInterceptor<T> interceptor, final CheckedFunction<ProxyConstructorFunction<T>, T> instantiate) throws ApplicationException {
        final ProxyConstructorFunction<T> enhancer = this.concreteOrAbstractEnhancer(interceptor);
        try {
            final T proxy = instantiate.apply(enhancer);
            if (this.typeDelegate() != null) this.restoreFields(this.typeDelegate(), proxy);
            return Result.of(proxy);
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

    protected Result<T> interfaceProxy(final StandardMethodInterceptor<T> interceptor) {
        final Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                this.defaultClassLoader(),
                this.proxyInterfaces(true),
                this.invocationHandler(interceptor));
        return Result.of(this.type().cast(proxy));
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
        return Result.of(Thread.currentThread()::getContextClassLoader)
                .orElse(JDKInterfaceProxyFactory.class::getClassLoader)
                .orElse(ClassLoader::getSystemClassLoader)
                .orElse(this.type()::getClassLoader)
                .orNull();
    }
}
