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

import org.dockbox.hartshorn.proxy.MethodInvokable;
import org.dockbox.hartshorn.proxy.ProxyMethodInterceptor;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

public class JavassistProxyMethodHandler<T> implements MethodHandler {

    private final ProxyMethodInterceptor<T> interceptor;

    public JavassistProxyMethodHandler(final ProxyMethodInterceptor<T> interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
        return this.interceptor.intercept(self,
                new MethodInvokable(thisMethod, this.interceptor().applicationContext()),
                new MethodInvokable(proceed, this.interceptor().applicationContext()),
                args);
    }

    public ProxyMethodInterceptor<T> interceptor() {
        return this.interceptor;
    }
}
