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

import org.dockbox.hartshorn.proxy.MethodWrapper.CallbackMethodWrapper;

public class StandardMethodWrapperFactory<T> implements MethodWrapperFactory<T> {

    private ProxyCallback<T> before;
    private ProxyCallback<T> after;
    private ProxyCallback<T> onError;

    private final ProxyFactory<T> factory;

    public StandardMethodWrapperFactory(final ProxyFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public MethodWrapperFactory<T> before(final ProxyCallback<T> callback) {
        this.before = callback;
        return this;
    }

    @Override
    public MethodWrapperFactory<T> after(final ProxyCallback<T> callback) {
        this.after = callback;
        return this;
    }

    @Override
    public MethodWrapperFactory<T> onError(final ProxyCallback<T> callback) {
        this.onError = callback;
        return this;
    }

    @Override
    public ProxyFactory<T> build() {
        return this.factory;
    }

    public MethodWrapper<T> create() {
        return new CallbackMethodWrapper<>(this.before, this.after, this.onError);
    }
}
