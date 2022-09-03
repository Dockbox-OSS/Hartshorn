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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;

public class ProxyCallbackContext<T> extends DefaultContext {

    private final T delegate;
    private final T proxy;
    private final MethodContext<?, T> method;
    private final Object[] args;
    private Throwable error;

    public ProxyCallbackContext(final T delegate, final T proxy, final MethodContext<?, T> method, final Object[] args) {
        this(delegate, proxy, method, args, null);
    }

    public ProxyCallbackContext(final T delegate, final T proxy, final MethodContext<?, T> method, final Object[] args, final Throwable error) {
        this.delegate = delegate;
        this.proxy = proxy;
        this.method = method;
        this.args = args;
        this.error = error;
    }

    @Nullable
    public T delegate() {
        return this.delegate;
    }

    public T proxy() {
        return this.proxy;
    }

    public MethodContext<?, T> method() {
        return this.method;
    }

    public Object[] args() {
        return this.args;
    }

    @Nullable
    public Throwable error() {
        return this.error;
    }

    public ProxyCallbackContext<T> acceptError(final Throwable error) {
        this.error = error;
        return this;
    }
}
