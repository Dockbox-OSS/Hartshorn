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

package org.dockbox.hartshorn.proxy.cglib;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.DefaultProxyFactory;
import org.dockbox.hartshorn.proxy.Invokable;
import org.dockbox.hartshorn.proxy.JDKInterfaceProxyFactory;
import org.dockbox.hartshorn.proxy.MethodInvokable;
import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;
import org.dockbox.hartshorn.proxy.StandardMethodInterceptor;

public class CglibProxyFactory<T> extends JDKInterfaceProxyFactory<T> {

    private static final NamingPolicy NAMING_POLICY = (prefix, className, key, names) -> DefaultProxyFactory.NAME_GENERATOR.get(prefix);

    public CglibProxyFactory(final Class<T> type, final ApplicationContext applicationContext) {
        super(type, applicationContext);
    }

    @Override
    protected ProxyConstructorFunction<T> concreteOrAbstractEnhancer(final StandardMethodInterceptor<T> interceptor) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.type());
        enhancer.setInterfaces(this.proxyInterfaces(false));
        enhancer.setNamingPolicy(NAMING_POLICY);
        enhancer.setClassLoader(this.defaultClassLoader());

        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            final MethodInvokable realMethod = new MethodInvokable(method);
            final Invokable proxyMethod = new ProxyMethodInvokable(proxy, obj, method);
            return interceptor.intercept(obj, realMethod, proxyMethod, args);
        });
        return () -> this.type().cast(enhancer.create());
    }
}