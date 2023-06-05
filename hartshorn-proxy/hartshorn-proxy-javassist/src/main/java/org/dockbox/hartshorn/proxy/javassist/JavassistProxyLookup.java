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

import org.dockbox.hartshorn.proxy.lookup.StandardProxyLookup;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

public class JavassistProxyLookup implements StandardProxyLookup {

    @Override
    public <T> Option<Class<T>> unproxy(final T instance) {
        if (instance instanceof Proxy proxy) {
            final MethodHandler handler = ProxyFactory.getHandler(proxy);
            if (handler instanceof JavassistProxyMethodHandler<?> javassistProxyMethodHandler) {
                final Class<?> targetClass = javassistProxyMethodHandler.interceptor().manager().targetClass();
                final Class<T> adjustedTargetClass = TypeUtils.adjustWildcards(targetClass, Class.class);
                return Option.of(adjustedTargetClass);
            }
        }
        return Option.empty();
    }

    @Override
    public boolean isProxy(final Class<?> candidate) {
        return ProxyFactory.isProxyClass(candidate);
    }
}
