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

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;
import org.dockbox.hartshorn.util.introspect.Introspector;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

/**
 * A Javassist {@link MethodHandler} that delegates to a {@link ProxyMethodInterceptor}.
 *
 * @param interceptor the interceptor to delegate to
 * @param introspector the introspector to use
 * @param <T> the type of the proxy
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public record JavassistProxyMethodHandler<T>(ProxyMethodInterceptor<T> interceptor, Introspector introspector) implements MethodHandler {

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        return this.interceptor.intercept(self,
                new MethodInvokable(thisMethod, this.introspector()),
                new MethodInvokable(proceed, this.introspector()),
                args);
    }
}
