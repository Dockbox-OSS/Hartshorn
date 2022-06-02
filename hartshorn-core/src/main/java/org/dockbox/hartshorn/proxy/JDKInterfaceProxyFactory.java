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
import org.dockbox.hartshorn.proxy.javassist.JavassistProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.InvocationHandler;

public abstract class JDKInterfaceProxyFactory<T> extends DefaultProxyFactory<T> {

    protected JDKInterfaceProxyFactory(final Class<T> type, final ApplicationContext applicationContext) {
        super(type, applicationContext);
    }

    @Override
    public Result<T> proxy() throws ApplicationException {
        final LazyProxyManager<T> manager = new LazyProxyManager<>(this.applicationContext(), this);
        final StandardMethodInterceptor<T> interceptor = new StandardMethodInterceptor<>(manager, this.applicationContext());

        final Result<T> proxy = this.type().isInterface()
                ? this.interfaceProxy(interceptor)
                : this.concreteOrAbstractProxy(interceptor);

        proxy.present(manager::proxy);
        return proxy;
    }

    protected InvocationHandler invocationHandler(final StandardMethodInterceptor<T> interceptor) {
        return (self, method, args) -> interceptor.intercept(self, new MethodInvokable(method), null, args);
    }

    protected abstract ProxyConstructorFunction<T> concreteOrAbstractEnhancer(StandardMethodInterceptor<T> interceptor);

    protected Result<T> concreteOrAbstractProxy(final StandardMethodInterceptor<T> interceptor) throws ApplicationException {
        final ProxyConstructorFunction<T> enhancer = this.concreteOrAbstractEnhancer(interceptor);
        try {
            final T proxy = enhancer.create();
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

    protected Result<T> interfaceProxy(final StandardMethodInterceptor interceptor) {
        final T proxy = (T) java.lang.reflect.Proxy.newProxyInstance(
                this.defaultClassLoader(),
                this.proxyInterfaces(true),
                this.invocationHandler(interceptor));
        return Result.of(proxy);
    }

    protected void restoreFields(final T existing, final T proxy) {
        final TypeContext<T> typeContext = this.applicationContext().environment().manager().isProxy(existing)
                ? TypeContext.of(this.type())
                : TypeContext.of(this.typeDelegate());
        for (final FieldContext<?> field : typeContext.fields()) {
            if (field.isStatic()) continue;
            field.set(proxy, field.get(existing).orNull());
        }
    }

    protected ClassLoader defaultClassLoader() {
        return Result.of(Thread.currentThread()::getContextClassLoader)
                .orElse(JavassistProxyFactory.class::getClassLoader)
                .orElse(ClassLoader::getSystemClassLoader)
                .orElse(this.type()::getClassLoader)
                .orNull();
    }
}
