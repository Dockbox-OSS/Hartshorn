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

import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.lookup.StandardProxyLookup;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.ProxyIntrospector;
import org.dockbox.hartshorn.util.option.Option;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

/**
 * A proxy lookup implementation that uses Javassist to determine whether a given instance is a proxy, and to retrieve
 * the target class of a proxy.
 *
 * <p>Proxies are detected by checking whether the given instance is an instance of {@link Proxy}, or if {@link Proxy}
 * is assignable from the class. For unproxying, it is assumed that the proxy is managed by a {@link
 * JavassistProxyMethodHandler}.
 *
 * @see ProxyFactory#isProxyClass(Class)
 * @see ProxyFactory#getHandler(Proxy)
 * @see Proxy
 * @see JavassistProxyMethodHandler
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public class JavassistProxyLookup implements StandardProxyLookup {

    @Override
    public <T> Option<Class<T>> unproxy(T instance) {
        if (instance instanceof Proxy proxy) {
            MethodHandler handler = ProxyFactory.getHandler(proxy);
            if (handler instanceof JavassistProxyMethodHandler<?> javassistProxyMethodHandler) {
                Class<?> targetClass = javassistProxyMethodHandler.interceptor().manager().targetClass();
                Class<T> adjustedTargetClass = TypeUtils.adjustWildcards(targetClass, Class.class);
                return Option.of(adjustedTargetClass);
            }
        }
        return Option.empty();
    }

    @Override
    public boolean isProxy(Class<?> candidate) {
        return ProxyFactory.isProxyClass(candidate);
    }

    @Override
    public <T> Option<ProxyIntrospector<T>> introspector(T instance) {
        if (instance instanceof Proxy proxy) {
            MethodHandler handler = ProxyFactory.getHandler(proxy);
            if (handler instanceof JavassistProxyMethodHandler<?> javassistProxyMethodHandler) {
                ProxyManager<?> manager = javassistProxyMethodHandler.interceptor().manager();
                if (manager.proxy() == instance) {
                    ProxyManager<T> adjustedManager = TypeUtils.adjustWildcards(manager, ProxyManager.class);
                    return Option.of(adjustedManager);
                }
            }
        }
        return Option.empty();
    }
}
