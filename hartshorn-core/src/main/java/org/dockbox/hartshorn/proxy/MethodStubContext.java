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
import org.dockbox.hartshorn.context.DefaultContext;

public class MethodStubContext<T> extends DefaultContext {

    // TODO: Determine what other information is needed here

    private final T self;
    private final Invokable source;
    private final Invokable target;
    private final ProxyMethodInterceptor<T> interceptor;
    private final Object[] args;

    public MethodStubContext(final T self,
                             final Invokable source,
                             final Invokable target,
                             final ProxyMethodInterceptor<T> interceptor,
                             final Object[] args) {
        this.self = self;
        this.source = source;
        this.target = target;
        this.interceptor = interceptor;
        this.args = args;
    }

    public T self() {
        return this.self;
    }

    public Invokable source() {
        return this.source;
    }

    public Invokable target() {
        return this.target;
    }

    public ProxyMethodInterceptor<T> interceptor() {
        return this.interceptor;
    }

    public ProxyManager<T> manager() {
        return this.interceptor.manager();
    }

    public ApplicationContext applicationContext() {
        return this.interceptor.applicationContext();
    }

    public Object[] args() {
        return this.args;
    }
}
