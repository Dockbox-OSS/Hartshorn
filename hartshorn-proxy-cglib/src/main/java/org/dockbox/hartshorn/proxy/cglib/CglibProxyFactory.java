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

package org.dockbox.hartshorn.proxy.cglib;

import org.dockbox.hartshorn.proxy.JDKInterfaceProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;
import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @deprecated CGLib is not actively maintained, and commonly causes issues with Java 9+.
 *             It is recommended to use Javassist instead, through the
 *             {@code org.dockbox.hartshorn.proxy.javassist.JavassistProxyFactory}.
 */
@Deprecated(since = "0.4.13")
public class CglibProxyFactory<T> extends JDKInterfaceProxyFactory<T> {

    private static final NamingPolicy NAMING_POLICY = (prefix, className, key, names) -> nameGenerator.get(prefix);

    public CglibProxyFactory(final Class<T> type, final CglibProxyOrchestrator proxyOrchestrator) {
        super(type, proxyOrchestrator);
    }

    @Override
    protected ProxyConstructorFunction<T> concreteOrAbstractEnhancer(final ProxyMethodInterceptor<T> interceptor) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.type());
        enhancer.setInterfaces(this.proxyInterfaces(false));
        enhancer.setNamingPolicy(NAMING_POLICY);
        enhancer.setClassLoader(this.defaultClassLoader());

        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            final MethodInvokable realMethod = new MethodInvokable(method, this.orchestrator().introspector());
            final Invokable proxyMethod = new CglibProxyMethodInvokable(this.orchestrator().introspector(), proxy, obj, method);
            return interceptor.intercept(obj, realMethod, proxyMethod, args);
        });
        return new CglibProxyConstructorFunction<>(this.type(), enhancer);
    }

}