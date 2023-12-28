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

package org.dockbox.hartshorn.proxy.javassist;

import org.dockbox.hartshorn.proxy.JDKInterfaceProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

/**
 * A proxy factory that uses Javassist to create proxies. This implementation is based on the
 * {@link JDKInterfaceProxyFactory}, but uses Javassist to create the proxy class if the type
 * is not an interface. This allows for the creation of proxies for concrete and abstract classes. The
 * proxy class is created by extending the target class, and implementing all interfaces that the
 * target class implements.
 *
 * @param <T> the type of the proxy
 *
 * @see ProxyFactory
 * @see MethodHandler
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public class JavassistProxyFactory<T> extends JDKInterfaceProxyFactory<T> {

    static {
        ProxyFactory.nameGenerator = classname -> nameGenerator.get(classname);
    }

    public JavassistProxyFactory(Class<T> type, JavassistProxyOrchestrator proxyOrchestrator) {
        super(type, proxyOrchestrator);
    }

    @Override
    protected ProxyConstructorFunction<T> concreteOrAbstractEnhancer(ProxyMethodInterceptor<T> interceptor) {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(this.type());
        factory.setInterfaces(this.proxyInterfaces(false));

        MethodHandler methodHandler = new JavassistProxyMethodHandler<>(interceptor, this.orchestrator().introspector());
        return new JavassistProxyConstructorFunction<>(this.type(), factory, methodHandler);
    }

}
